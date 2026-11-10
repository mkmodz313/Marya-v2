package com.myra.assistant.control

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.view.accessibility.AccessibilityNodeInfo
import com.myra.assistant.apps.AppLauncher
import com.myra.assistant.apps.InstalledAppsManager
import com.myra.assistant.data.ChatRepository
import com.myra.assistant.service.AccessibilityHelperService
import com.myra.assistant.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Natural-language phone control for typed AND spoken commands.
 *
 * Everything goes through official Android Intents / wa.me deep links. WhatsApp messages are NEVER sent
 * without an explicit "yes" from the user, and the Accessibility Service is only used (after that "yes")
 * to tap WhatsApp's own Send button. Nothing here bypasses permissions or app security.
 *
 * Returns handled=false for anything it does not recognise so the existing command pipeline keeps working.
 */
object PhoneControl {

    private const val TAG = "PHONE_CTRL"
    private const val PENDING_TTL_MS = 120_000L

    data class Outcome(val handled: Boolean, val reply: String = "", val success: Boolean = true)

    private val NOT_HANDLED = Outcome(false)

    private data class Pending(val number: String, val text: String, val createdAt: Long)

    private enum class Target(val label: String) {
        WHATSAPP("WhatsApp"), YOUTUBE("YouTube"), GOOGLE("Google"), CHROME("Chrome"),
        FACEBOOK("Facebook"), INSTAGRAM("Instagram"), GALLERY("Gallery"), CAMERA("Camera"),
        SETTINGS("Settings"), CALCULATOR("Calculator")
    }

    private val ALIASES = mapOf(
        "whatsapp" to Target.WHATSAPP, "whats app" to Target.WHATSAPP, "watsapp" to Target.WHATSAPP,
        "youtube" to Target.YOUTUBE, "you tube" to Target.YOUTUBE,
        "google" to Target.GOOGLE,
        "chrome" to Target.CHROME, "google chrome" to Target.CHROME,
        "facebook" to Target.FACEBOOK, "fb" to Target.FACEBOOK,
        "instagram" to Target.INSTAGRAM, "insta" to Target.INSTAGRAM,
        "gallery" to Target.GALLERY, "photos" to Target.GALLERY,
        "camera" to Target.CAMERA,
        "settings" to Target.SETTINGS, "setting" to Target.SETTINGS,
        "calculator" to Target.CALCULATOR, "calc" to Target.CALCULATOR
    )

    private val PACKAGES = mapOf(
        Target.WHATSAPP to listOf("com.whatsapp", "com.whatsapp.w4b"),
        Target.YOUTUBE to listOf("com.google.android.youtube"),
        Target.GOOGLE to listOf("com.google.android.googlequicksearchbox"),
        Target.CHROME to listOf("com.android.chrome"),
        Target.FACEBOOK to listOf("com.facebook.katana"),
        Target.INSTAGRAM to listOf("com.instagram.android")
    )

    private val YES_WORDS = setOf("yes", "yeah", "yep", "ok", "okay", "confirm", "confirmed", "send", "haan", "han", "ji", "bhej", "bhejo", "kardo", "sure")
    private val NO_WORDS = setOf("no", "nope", "cancel", "nahi", "nahin", "stop", "ruko", "dont", "don", "mat")

    private val COMPOSE_A = Regex(
        "^\\s*(?:please\\s+)?(?:send|write|type|compose)\\s+(?:this|a|the|my)?\\s*(?:message|msg|text)\\s*(?:saying|that says|is)?\\s*[:,\\-]?\\s*(.+)$",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )
    private val COMPOSE_B = Regex(
        "^\\s*(?:message|msg)\\s+(?:bhejo|bhej\\s*do|send\\s*karo|likho)\\s*[:,\\-]?\\s*(.+)$",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )
    private val PHONE_RX = Regex("(\\+?\\d[\\d\\s\\-().]{6,}\\d)")
    private val OPEN_RX1 = Regex(
        "^(?:please |myra |hey myra )*(?:open|launch|start|kholo|khol|chalao)\\s+(?:the )?(.+?)(?:\\s+(?:app|application|karo|kardo|kar do|do|please))*$"
    )
    private val OPEN_RX2 = Regex(
        "^(.+?)\\s+(?:app )?(?:kholo|khol do|khol|open karo|open kar do|open|chalao)(?:\\s+(?:karo|kardo|please|do))*$"
    )
    private val SEARCH_ON = Regex("^(?:please )?search\\s+(?:for )?(.+?)\\s+(?:on|in)\\s+(google|youtube)$")
    private val SEARCH_PLAIN = Regex("^(?:please )?(?:search|google)\\s+(?:for )?(.+)$")
    private val SEARCH_HINGLISH = Regex("^(.+?)\\s+(?:ko )?(google|youtube)\\s+(?:par|pe)\\s+search(?:\\s+karo)?$")
    private val DOMAIN_RX = Regex(
        "((?:https?://)?(?:www\\.)?[a-z0-9][a-z0-9-]*(?:\\.[a-z0-9-]+)*\\.(?:com|org|net|pk|in|io|co|edu|gov|app|dev|ai|info|tv|me|uk)(?:/\\S*)?)"
    )

    private var pending: Pending? = null
    private var activeNumber: String? = null
    private var lastActionKey = ""
    private var lastActionAt = 0L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val main = Handler(Looper.getMainLooper())

    // ─────────────────────────────────────────────────────────────────────────────────────────────
    @Synchronized
    fun handle(context: Context, raw: String): Outcome {
        val ctx = context.applicationContext
        val text = raw.trim()
        if (text.isEmpty()) return NOT_HANDLED
        val lower = normalize(text)

        // 1) Compose a WhatsApp message (needs a chat opened by number first; a new compose replaces an older pending one)
        val composed = COMPOSE_A.find(text)?.groupValues?.get(1) ?: COMPOSE_B.find(text)?.groupValues?.get(1)
        if (!composed.isNullOrBlank()) return prepareMessage(composed.trim())

        // 2) Confirmation / cancellation of a prepared WhatsApp message
        pending?.let { p ->
            if (System.currentTimeMillis() - p.createdAt > PENDING_TTL_MS) {
                pending = null
            } else {
                val words = lower.split(" ").filter { it.isNotEmpty() }
                if (words.size <= 4) {
                    if (words.any { it in NO_WORDS }) {
                        pending = null
                        return Outcome(true, "Okay dear, message cancel kar diya. Maine kuch bhi send nahi kiya.")
                    }
                    if (words.any { it in YES_WORDS }) return confirmSend(ctx, p)
                }
            }
        }

        // 3) Open a WhatsApp chat by phone number
        val phoneMatch = PHONE_RX.find(text)
        if (phoneMatch != null && !lower.startsWith("call") &&
            listOf("chat", "whatsapp", "message", "msg", "open", "kholo", "baat").any { lower.contains(it) }
        ) {
            return openChatByNumber(ctx, phoneMatch.value)
        }

        // 4) Search
        SEARCH_ON.find(lower)?.let { return search(ctx, it.groupValues[1], it.groupValues[2]) }
        SEARCH_HINGLISH.find(lower)?.let { return search(ctx, it.groupValues[1], it.groupValues[2]) }
        SEARCH_PLAIN.find(lower)?.let { return search(ctx, it.groupValues[1], "google") }

        // 5) Open a website
        val opensSomething = listOf("open", "launch", "kholo", "khol", "website", "site", "visit", "go to").any { lower.contains(it) }
        if (opensSomething) {
            val spoken = lower.replace(" dot ", ".")
            DOMAIN_RX.find(spoken)?.let { return openWebsite(ctx, it.value) }
        }

        // 6) Open an app ("open X" / "X kholo"). Unknown app names are only accepted in the verb-first form.
        val verbFirst = OPEN_RX1.find(lower)?.groupValues?.get(1)?.trim()
        if (!verbFirst.isNullOrEmpty() && verbFirst.split(" ").size <= 3) return openApp(ctx, verbFirst, allowGeneric = true)
        val verbLast = OPEN_RX2.find(lower)?.groupValues?.get(1)?.trim()
        if (!verbLast.isNullOrEmpty() && ALIASES.containsKey(verbLast)) return openApp(ctx, verbLast, allowGeneric = false)

        return NOT_HANDLED
    }

    fun hasPendingMessage(): Boolean = pending != null

    // ─────────────────────────────────────────────────────────────────────────────────────────────
    // WhatsApp
    // ─────────────────────────────────────────────────────────────────────────────────────────────
    private fun openChatByNumber(ctx: Context, candidate: String): Outcome {
        val hasPlus = candidate.trim().startsWith("+")
        var digits = candidate.filter { it.isDigit() }
        if (digits.startsWith("00")) digits = digits.drop(2)
        else if (!hasPlus && digits.startsWith("0")) {
            return Outcome(true, "Dear, number ke saath country code bhi batayein, jaise +92 300 1234567. Isse main sahi chat khol sakungi.", false)
        }
        if (digits.length !in 8..15) {
            return Outcome(true, "Ye number sahi nahi lag raha. Please country code ke saath dobara batayein.", false)
        }
        val pkg = whatsAppPackage(ctx)
            ?: return Outcome(true, "WhatsApp is phone par installed nahi hai dear.", false)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$digits")).setPackage(pkg)
        if (!start(ctx, intent)) return Outcome(true, "WhatsApp chat khul nahi paayi. Please dobara try karein.", false)
        activeNumber = digits
        pending = null
        return Outcome(true, "Sure dear! Opening WhatsApp chat with +$digits.")
    }

    private fun prepareMessage(message: String): Outcome {
        val number = activeNumber
            ?: return Outcome(true, "Pehle batayein kis number par message bhejna hai, jaise: \"Open a chat with +92 300 1234567\".", false)
        pending = Pending(number, message, System.currentTimeMillis())
        return Outcome(
            true,
            "Message ready for +$number:\n\"$message\"\nShall I send it? Say \"yes\" to confirm or \"no\" to cancel."
        )
    }

    private fun confirmSend(ctx: Context, p: Pending): Outcome {
        pending = null
        val pkg = whatsAppPackage(ctx)
            ?: return Outcome(true, "WhatsApp installed nahi hai dear.", false)
        val uri = Uri.parse("https://wa.me/${p.number}?text=${Uri.encode(p.text)}")
        if (!start(ctx, Intent(Intent.ACTION_VIEW, uri).setPackage(pkg))) {
            return Outcome(true, "WhatsApp khul nahi paaya, message send nahi hua.", false)
        }
        if (!AccessibilityHelperService.isEnabled(ctx)) {
            return Outcome(
                true,
                "Message WhatsApp mein ready hai. Accessibility service band hai, isliye Send button aap khud dabayein.",
                false
            )
        }
        // Accessibility is used ONLY now, after explicit confirmation, to tap WhatsApp's own Send button.
        scope.launch {
            val tapped = tapWhatsAppSend()
            ChatRepository.addAssistant(
                if (tapped) "✅ Send button dabaa diya (+${p.number}). Delivery WhatsApp mein check kar lein."
                else "⚠️ Message WhatsApp mein ready hai, lekin main Send button nahi dabaa saki. Please aap Send dabayein."
            )
        }
        return Outcome(true, "Okay dear! WhatsApp mein message khol rahi hoon, Send dabane ki koshish karti hoon…")
    }

    private suspend fun tapWhatsAppSend(): Boolean {
        repeat(25) {
            delay(400)
            val root = AccessibilityHelperService.getFreshRoot() ?: return@repeat
            if (root.packageName?.toString()?.startsWith("com.whatsapp") != true) return@repeat
            val node = findSendButton(root) ?: return@repeat
            return clickNodeOrParent(node)
        }
        return false
    }

    private fun findSendButton(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        for (id in listOf("com.whatsapp:id/send", "com.whatsapp.w4b:id/send")) {
            root.findAccessibilityNodeInfosByViewId(id)?.firstOrNull()?.let { return it }
        }
        return root.findAccessibilityNodeInfosByText("Send")?.firstOrNull {
            it.contentDescription?.toString().equals("Send", true) || it.text?.toString().equals("Send", true)
        }
    }

    private fun clickNodeOrParent(node: AccessibilityNodeInfo): Boolean {
        var n: AccessibilityNodeInfo? = node
        var hops = 0
        while (n != null && hops < 4) {
            if (n.isClickable) return n.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            n = n.parent
            hops++
        }
        return false
    }

    private fun whatsAppPackage(ctx: Context): String? =
        PACKAGES[Target.WHATSAPP]?.firstOrNull { ctx.packageManager.getLaunchIntentForPackage(it) != null }

    // ─────────────────────────────────────────────────────────────────────────────────────────────
    // Apps / websites / search
    // ─────────────────────────────────────────────────────────────────────────────────────────────
    private fun openApp(ctx: Context, name: String, allowGeneric: Boolean): Outcome {
        val target = ALIASES[name]
        if (target == null) {
            if (!allowGeneric) return NOT_HANDLED
            // Any other installed app: reuse the existing app finder/launcher (strict name match only).
            val found = InstalledAppsManager.findApp(ctx, name) ?: return NOT_HANDLED
            if (!(found.name.contains(name) || name.contains(found.name))) return NOT_HANDLED
            return if (AppLauncher.launch(ctx, found.label)) Outcome(true, "Sure dear! Opening ${found.label}.")
            else Outcome(true, "Sorry dear, ${found.label} open nahi ho paaya.", false)
        }
        if (isDuplicate("open:${target.name}")) return Outcome(true, "Sure dear! Opening ${target.label}.")

        val ok = when (target) {
            Target.GALLERY -> start(ctx, Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY))
            Target.CAMERA -> start(ctx, Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
            Target.SETTINGS -> start(ctx, Intent(Settings.ACTION_SETTINGS))
            Target.CALCULATOR -> start(ctx, Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALCULATOR))
            else -> {
                val pkg = PACKAGES[target]?.firstOrNull { ctx.packageManager.getLaunchIntentForPackage(it) != null }
                pkg != null && start(ctx, ctx.packageManager.getLaunchIntentForPackage(pkg)!!)
            }
        }
        return if (ok) Outcome(true, "Sure dear! Opening ${target.label}.")
        else Outcome(true, "Sorry dear, ${target.label} open nahi ho paaya. Shayad ye app is phone par installed nahi hai.", false)
    }

    private fun openWebsite(ctx: Context, domain: String): Outcome {
        val url = if (domain.startsWith("http")) domain else "https://$domain"
        if (isDuplicate("web:$url")) return Outcome(true, "Sure dear! Opening $domain.")
        return if (start(ctx, Intent(Intent.ACTION_VIEW, Uri.parse(url)))) Outcome(true, "Sure dear! Opening $domain.")
        else Outcome(true, "Website open nahi ho saki.", false)
    }

    private fun search(ctx: Context, query: String, site: String): Outcome {
        val q = query.trim()
        if (q.isEmpty()) return NOT_HANDLED
        val url = if (site == "youtube") "https://www.youtube.com/results?search_query=${Uri.encode(q)}"
        else "https://www.google.com/search?q=${Uri.encode(q)}"
        if (isDuplicate("search:$site:$q")) return Outcome(true, "Searching $q.")
        return if (start(ctx, Intent(Intent.ACTION_VIEW, Uri.parse(url)))) Outcome(true, "Sure dear! Searching \"$q\" on ${site.replaceFirstChar { it.uppercase() }}.")
        else Outcome(true, "Search open nahi ho saki.", false)
    }

    // ─────────────────────────────────────────────────────────────────────────────────────────────
    private fun start(ctx: Context, intent: Intent): Boolean = try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
        true
    } catch (e: Exception) {
        Logger.e(TAG, "Launch failed: ${e.message}")
        false
    }

    /** The existing command pipeline may also react to the same sentence; never fire the same action twice in 4 s. */
    private fun isDuplicate(key: String): Boolean {
        val now = System.currentTimeMillis()
        val dup = key == lastActionKey && now - lastActionAt < 4000
        lastActionKey = key
        lastActionAt = now
        return dup
    }

    private fun normalize(s: String): String =
        s.lowercase().replace(Regex("[^\\p{L}\\p{N}+\\s.]"), " ").replace(Regex("\\s+"), " ").trim().trimEnd('.')
}
