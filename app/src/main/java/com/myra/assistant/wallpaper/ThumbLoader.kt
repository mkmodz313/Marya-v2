package com.myra.assistant.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import okhttp3.Request
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.Executors

/**
 * Thumbnail loader: memory cache + PERSISTENT disk cache (filesDir/thumbs).
 * Once a thumbnail has been shown it is saved on the phone, so after the app is closed / re-opened every
 * wallpaper that was already seen appears immediately (no blank cards, no waiting for the network).
 * Thumbnails only - never videos.
 */
object ThumbLoader {
    private val cache = object : LruCache<String, Bitmap>(32 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }
    private val pool = Executors.newFixedThreadPool(3)
    private val main = Handler(Looper.getMainLooper())

    fun load(context: Context, url: String, into: ImageView) {
        into.tag = url
        if (url.isEmpty()) { into.setImageDrawable(null); return }
        cache.get(url)?.let { into.setImageBitmap(it); return }
        into.setImageDrawable(null)
        val app = context.applicationContext
        pool.execute {
            val bmp = try { obtain(app, url) } catch (_: Throwable) { null }
            main.post { if (into.tag == url && bmp != null) into.setImageBitmap(bmp) }
        }
    }

    /** Loads thumbnails into memory in the background (called at app start / gallery open) so cards appear instantly. */
    fun prewarm(context: Context, urls: List<String>) {
        val app = context.applicationContext
        urls.filter { it.isNotEmpty() }.forEach { url ->
            if (cache.get(url) != null) return@forEach
            pool.execute { try { obtain(app, url) } catch (_: Throwable) { } }
        }
    }

    /** memory -> disk -> network (network result is written to disk). */
    private fun obtain(ctx: Context, url: String): Bitmap? {
        cache.get(url)?.let { return it }
        val file = diskFile(ctx, url)
        var bytes: ByteArray? = null
        if (!url.startsWith("asset:") && file.exists() && file.length() > 0) {
            bytes = try { file.readBytes() } catch (_: Exception) { null }
        }
        var fromNetwork = false
        if (bytes == null) {
            bytes = if (url.startsWith("asset:")) {
                ctx.assets.open(url.removePrefix("asset:")).use { it.readBytes() }
            } else {
                fromNetwork = true
                WallpaperRepository.http.newCall(Request.Builder().url(url).build()).execute().use { r ->
                    if (!r.isSuccessful) return null
                    r.body?.bytes() ?: return null
                }
            }
        }
        val bmp = decode(bytes!!)
        if (bmp == null) {
            if (!fromNetwork && file.exists()) file.delete()   // corrupt cache file - fetch again next time
            return null
        }
        cache.put(url, bmp)
        if (fromNetwork) {
            try {
                val tmp = File(file.parentFile, file.name + ".tmp")
                tmp.writeBytes(bytes)
                if (!tmp.renameTo(file)) tmp.delete()
            } catch (_: Exception) { }
        }
        return bmp
    }

    private fun decode(bytes: ByteArray): Bitmap? {
        val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, o)
        var sample = 1
        while (o.outWidth / sample > 720) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    }

    private fun diskFile(ctx: Context, url: String): File {
        val dir = File(ctx.filesDir, "thumbs").apply { mkdirs() }
        val name = MessageDigest.getInstance("MD5").digest(url.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return File(dir, "$name.img")
    }
}
