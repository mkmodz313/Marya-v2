package com.myra.assistant.wallpaper

import android.content.Context
import com.myra.assistant.utils.prefs
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Catalog + downloads. Talks to Cloud Firestore through its public REST API (no extra SDK / plugin needed,
 * so the project still builds without google-services.json). Only published wallpapers are readable by the
 * app - that rule is enforced server-side by firestore.rules.
 *
 * Only thumbnails are fetched for the gallery. A video is downloaded ONLY when the user taps SET WALLPAPER;
 * the detail preview streams it instead.
 */
object WallpaperRepository {

    private const val CACHE_KEY = "wp_catalog_cache"
    private val JSON = "application/json".toMediaType()

    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun base() =
        "https://firestore.googleapis.com/v1/projects/${CloudConfig.FIREBASE_PROJECT_ID}/databases/(default)/documents"

    /** Last catalog stored on the phone (shown instantly / offline). */
    fun cached(context: Context): List<WallpaperItem> = try {
        val arr = JSONArray(context.prefs().getString(CACHE_KEY, "[]"))
        (0 until arr.length()).map { WallpaperItem.fromJson(arr.getJSONObject(it)) }
    } catch (_: Exception) { emptyList() }

    /** Fetches published wallpapers from the cloud. Throws on network / server errors. */
    fun fetchCloud(context: Context): List<WallpaperItem> {
        if (!CloudConfig.isConfigured) return emptyList()
        val body = JSONObject().put(
            "structuredQuery", JSONObject()
                .put("from", JSONArray().put(JSONObject().put("collectionId", "wallpapers")))
                .put("where", JSONObject().put("fieldFilter", JSONObject()
                    .put("field", JSONObject().put("fieldPath", "published"))
                    .put("op", "EQUAL")
                    .put("value", JSONObject().put("booleanValue", true))))
                .put("limit", 200)
        ).toString()
        val req = Request.Builder()
            .url("${base()}:runQuery?key=${CloudConfig.FIREBASE_API_KEY}")
            .post(body.toRequestBody(JSON)).build()
        http.newCall(req).execute().use { r ->
            val text = r.body?.string().orEmpty()
            if (!r.isSuccessful) throw RuntimeException("Catalog error ${r.code}")
            val arr = JSONArray(text)
            val out = ArrayList<WallpaperItem>()
            for (i in 0 until arr.length()) {
                val doc = arr.getJSONObject(i).optJSONObject("document") ?: continue
                parse(doc)?.let { out.add(it) }
            }
            out.sortByDescending { it.createdAt }
            context.prefs().edit().putString(CACHE_KEY, JSONArray(out.map { it.toJson() }).toString()).apply()
            return out
        }
    }

    private fun parse(doc: JSONObject): WallpaperItem? {
        val f = doc.optJSONObject("fields") ?: return null
        fun s(k: String) = f.optJSONObject(k)?.optString("stringValue", "") ?: ""
        fun n(k: String) = f.optJSONObject(k)?.let { it.optString("integerValue", it.optString("doubleValue", "0")) }?.toDoubleOrNull()?.toLong() ?: 0L
        val id = doc.optString("name").substringAfterLast('/')
        val video = s("videoUrl")
        if (id.isEmpty() || video.isEmpty()) return null
        val created = f.optJSONObject("createdAt")?.let { it.optString("integerValue", "").toLongOrNull() } ?: 0L
        return WallpaperItem(id, s("title"), s("description"), s("category").ifEmpty { "General" },
            video, s("thumbUrl"), n("sizeBytes"), created, n("downloads"), n("views"))
    }

    /** Fire-and-forget statistics (+1). Server rules only allow +1 on these two counters of published items. */
    fun bump(item: WallpaperItem, field: String) {
        if (item.isBundled || !CloudConfig.isConfigured) return
        try {
            val path = "projects/${CloudConfig.FIREBASE_PROJECT_ID}/databases/(default)/documents/wallpapers/${item.id}"
            val body = JSONObject().put("writes", JSONArray().put(JSONObject().put("transform", JSONObject()
                .put("document", path)
                .put("fieldTransforms", JSONArray().put(JSONObject()
                    .put("fieldPath", field)
                    .put("increment", JSONObject().put("integerValue", "1")))))))
            val req = Request.Builder()
                .url("${base()}:commit?key=${CloudConfig.FIREBASE_API_KEY}")
                .post(body.toString().toRequestBody(JSON)).build()
            http.newCall(req).execute().close()
        } catch (_: Exception) { }
    }

    fun localFile(context: Context, item: WallpaperItem) = File(File(context.filesDir, "wallpapers").apply { mkdirs() }, "${item.id}.mp4")

    /**
     * Makes the video available as a local file (copies the bundled sample or downloads the cloud video).
     * [onProgress] gets 0..100. Returns the file or throws.
     */
    fun ensureVideo(context: Context, item: WallpaperItem, onProgress: (Int) -> Unit): File {
        val out = localFile(context, item)
        if (out.exists() && out.length() > 0) { onProgress(100); return out }
        val tmp = File(out.parentFile, "${item.id}.part")
        try {
            if (item.isBundled) {
                context.assets.open(item.videoUrl.removePrefix("asset:")).use { i ->
                    FileOutputStream(tmp).use { o -> i.copyTo(o) }
                }
                onProgress(100)
            } else {
                val req = Request.Builder().url(item.videoUrl).build()
                http.newCall(req).execute().use { r ->
                    if (!r.isSuccessful) throw RuntimeException("Download failed (${r.code})")
                    val body = r.body ?: throw RuntimeException("Empty response")
                    val total = body.contentLength()
                    var done = 0L
                    body.byteStream().use { i ->
                        FileOutputStream(tmp).use { o ->
                            val buf = ByteArray(64 * 1024)
                            while (true) {
                                val n = i.read(buf)
                                if (n < 0) break
                                o.write(buf, 0, n)
                                done += n
                                if (total > 0) onProgress((done * 100 / total).toInt())
                            }
                        }
                    }
                }
            }
            if (!tmp.renameTo(out)) throw RuntimeException("Could not save video")
            return out
        } finally {
            if (tmp.exists()) tmp.delete()
        }
    }
}
