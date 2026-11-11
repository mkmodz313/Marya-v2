package com.myra.assistant.admin

import android.net.Uri
import com.myra.assistant.wallpaper.CloudConfig
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.TimeUnit

data class AdminWallpaper(
    val id: String, val title: String, val description: String, val category: String,
    val videoUrl: String, val thumbUrl: String, val videoPath: String, val thumbPath: String,
    val sizeBytes: Long, val createdAt: Long, val downloads: Long, val views: Long, val published: Boolean
)

/**
 * Admin backend calls (Firebase Auth + Firestore + Storage over their public REST APIs).
 *
 * The admin password is typed by the administrator on the login screen, sent over HTTPS to Google's
 * sign-in endpoint and never stored - not in the APK, not on the phone. All permissions are enforced by
 * the Firestore/Storage security rules on Google's servers (only ADMIN_EMAIL can write).
 */
object AdminApi {

    class ApiError(message: String) : Exception(message)

    private val JSON = "application/json".toMediaType()
    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(0, TimeUnit.SECONDS)
        .callTimeout(0, TimeUnit.SECONDS)
        .build()

    @Volatile private var idToken: String? = null
    @Volatile private var refreshToken: String? = null
    @Volatile private var expiresAt = 0L

    fun isSignedIn() = idToken != null
    fun signOut() { idToken = null; refreshToken = null; expiresAt = 0L }

    private val key get() = CloudConfig.FIREBASE_API_KEY
    private fun docsBase() =
        "https://firestore.googleapis.com/v1/projects/${CloudConfig.FIREBASE_PROJECT_ID}/databases/(default)/documents"

    // ── Auth ─────────────────────────────────────────────────────────────────────────────────────
    fun signIn(email: String, password: String) {
        if (!email.trim().equals(CloudConfig.ADMIN_EMAIL, ignoreCase = true)) throw ApiError("Ye account admin nahi hai.")
        val body = JSONObject().put("email", email.trim()).put("password", password).put("returnSecureToken", true)
        val req = Request.Builder()
            .url("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$key")
            .post(body.toString().toRequestBody(JSON)).build()
        http.newCall(req).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw ApiError(authMessage(text))
            val o = JSONObject(text)
            if (!o.optString("email").equals(CloudConfig.ADMIN_EMAIL, true)) throw ApiError("Ye account admin nahi hai.")
            idToken = o.getString("idToken")
            refreshToken = o.optString("refreshToken")
            expiresAt = System.currentTimeMillis() + o.optString("expiresIn", "3600").toLong() * 1000 - 90_000
        }
    }

    private fun authMessage(body: String): String {
        val m = try { JSONObject(body).getJSONObject("error").optString("message") } catch (_: Exception) { "" }
        return when {
            m.contains("INVALID_LOGIN_CREDENTIALS") || m.contains("INVALID_PASSWORD") || m.contains("EMAIL_NOT_FOUND") ->
                "Email ya password galat hai."
            m.contains("OPERATION_NOT_ALLOWED") -> "Firebase mein Email/Password sign-in enable nahi hai."
            m.contains("TOO_MANY_ATTEMPTS") -> "Bohot zyada koshishen. Thodi der baad try karein."
            m.contains("USER_DISABLED") -> "Ye account disable hai."
            m.contains("API_KEY") -> "Firebase API key sahi nahi hai."
            m.isNotEmpty() -> "Login failed: $m"
            else -> "Login failed. Internet check karein."
        }
    }

    private fun refresh() {
        val rt = refreshToken ?: throw ApiError("Session khatam. Dobara login karein.")
        val req = Request.Builder()
            .url("https://securetoken.googleapis.com/v1/token?key=$key")
            .post(FormBody.Builder().add("grant_type", "refresh_token").add("refresh_token", rt).build()).build()
        http.newCall(req).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) { signOut(); throw ApiError("Session khatam. Dobara login karein.") }
            val o = JSONObject(text)
            idToken = o.getString("id_token")
            refreshToken = o.optString("refresh_token", rt)
            expiresAt = System.currentTimeMillis() + o.optString("expires_in", "3600").toLong() * 1000 - 90_000
        }
    }

    @Synchronized
    private fun token(): String {
        if (idToken == null) throw ApiError("Login zaroori hai.")
        if (System.currentTimeMillis() > expiresAt) refresh()
        return idToken ?: throw ApiError("Login zaroori hai.")
    }

    private fun httpError(code: Int, body: String, what: String): ApiError {
        val msg = try { JSONObject(body).getJSONObject("error").optString("message") } catch (_: Exception) { "" }
        return ApiError(
            when (code) {
                401 -> "Session khatam. Dobara login karein."
                403 -> "$what: permission nahi mili. Firebase mein rules Publish karein aur admin email se login karein."
                404 -> "$what: nahi mila. Firestore Database / Storage Firebase console mein create kiya hai?"
                else -> "$what failed ($code) $msg"
            }
        )
    }

    // ── Firestore ────────────────────────────────────────────────────────────────────────────────
    private fun fStr(s: String) = JSONObject().put("stringValue", s)
    private fun fInt(l: Long) = JSONObject().put("integerValue", l.toString())
    private fun fBool(b: Boolean) = JSONObject().put("booleanValue", b)

    fun list(): List<AdminWallpaper> {
        val req = Request.Builder().url("${docsBase()}/wallpapers?pageSize=300")
            .header("Authorization", "Bearer ${token()}").build()
        http.newCall(req).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw httpError(r.code, text, "Wallpapers list")
            val arr = JSONObject(text).optJSONArray("documents") ?: JSONArray()
            val out = ArrayList<AdminWallpaper>()
            for (i in 0 until arr.length()) {
                val d = arr.getJSONObject(i)
                val f = d.optJSONObject("fields") ?: continue
                fun s(k: String) = f.optJSONObject(k)?.optString("stringValue", "") ?: ""
                fun n(k: String) = f.optJSONObject(k)?.optString("integerValue", "0")?.toLongOrNull() ?: 0L
                out.add(AdminWallpaper(
                    d.optString("name").substringAfterLast('/'), s("title"), s("description"), s("category").ifEmpty { "General" },
                    s("videoUrl"), s("thumbUrl"), s("videoPath"), s("thumbPath"),
                    n("sizeBytes"), n("createdAt"), n("downloads"), n("views"),
                    f.optJSONObject("published")?.optBoolean("booleanValue", false) ?: false
                ))
            }
            return out.sortedByDescending { it.createdAt }
        }
    }

    fun create(w: AdminWallpaper) {
        val fields = JSONObject()
            .put("title", fStr(w.title)).put("description", fStr(w.description)).put("category", fStr(w.category))
            .put("videoUrl", fStr(w.videoUrl)).put("thumbUrl", fStr(w.thumbUrl))
            .put("videoPath", fStr(w.videoPath)).put("thumbPath", fStr(w.thumbPath))
            .put("sizeBytes", fInt(w.sizeBytes)).put("createdAt", fInt(w.createdAt))
            .put("downloads", fInt(0)).put("views", fInt(0)).put("published", fBool(w.published))
        patch(w.id, fields, null)
    }

    fun update(id: String, title: String? = null, description: String? = null, category: String? = null, published: Boolean? = null) {
        val fields = JSONObject(); val mask = ArrayList<String>()
        title?.let { fields.put("title", fStr(it)); mask.add("title") }
        description?.let { fields.put("description", fStr(it)); mask.add("description") }
        category?.let { fields.put("category", fStr(it)); mask.add("category") }
        published?.let { fields.put("published", fBool(it)); mask.add("published") }
        if (mask.isNotEmpty()) patch(id, fields, mask)
    }

    private fun patch(id: String, fields: JSONObject, mask: List<String>?) {
        val q = mask?.joinToString("&") { "updateMask.fieldPaths=$it" }?.let { "?$it" } ?: ""
        val req = Request.Builder().url("${docsBase()}/wallpapers/$id$q")
            .header("Authorization", "Bearer ${token()}")
            .patch(JSONObject().put("fields", fields).toString().toRequestBody(JSON)).build()
        http.newCall(req).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw httpError(r.code, text, "Save")
        }
    }

    fun deleteDoc(id: String) {
        val req = Request.Builder().url("${docsBase()}/wallpapers/$id")
            .header("Authorization", "Bearer ${token()}").delete().build()
        http.newCall(req).execute().use { r ->
            if (!r.isSuccessful) throw httpError(r.code, r.body?.string().orEmpty(), "Delete")
        }
    }

    // ── Storage ──────────────────────────────────────────────────────────────────────────────────
    private class StreamBody(
        val type: MediaType?, val size: Long, val open: () -> InputStream, val onProgress: (Int) -> Unit
    ) : RequestBody() {
        override fun contentType() = type
        override fun contentLength() = size
        override fun writeTo(sink: BufferedSink) {
            open().use { input ->
                val buf = ByteArray(64 * 1024)
                var done = 0L
                var last = -1
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    sink.write(buf, 0, n)
                    done += n
                    if (size > 0) {
                        val pct = (done * 100 / size).toInt()
                        if (pct != last) { last = pct; onProgress(pct) }
                    }
                }
            }
        }
    }

    /** Uploads a file to Firebase Storage (streamed, never loaded fully into memory). Returns its public download URL. */
    fun upload(path: String, mime: String, size: Long, open: () -> InputStream, onProgress: (Int) -> Unit): String {
        val bucket = CloudConfig.STORAGE_BUCKET
        val req = Request.Builder()
            .url("https://firebasestorage.googleapis.com/v0/b/$bucket/o?name=${Uri.encode(path)}")
            .header("Authorization", "Firebase ${token()}")
            .post(StreamBody(mime.toMediaTypeOrNull(), size, open, onProgress)).build()
        http.newCall(req).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw httpError(r.code, text, "Upload")
            val o = JSONObject(text)
            val tokens = o.optString("downloadTokens", "")
            if (tokens.isEmpty()) throw ApiError("Upload hua lekin download link nahi mila.")
            return "https://firebasestorage.googleapis.com/v0/b/$bucket/o/${Uri.encode(path)}?alt=media&token=${tokens.substringBefore(',')}"
        }
    }

    fun deleteFile(path: String) {
        if (path.isEmpty()) return
        val req = Request.Builder()
            .url("https://firebasestorage.googleapis.com/v0/b/${CloudConfig.STORAGE_BUCKET}/o/${Uri.encode(path)}")
            .header("Authorization", "Firebase ${token()}").delete().build()
        try { http.newCall(req).execute().close() } catch (_: Exception) { }
    }
}
