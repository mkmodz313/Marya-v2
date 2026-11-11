package com.myra.assistant.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import okhttp3.Request
import java.util.concurrent.Executors

/** Tiny thumbnail loader (memory cache + background decode with down-sampling). Thumbnails only - never videos. */
object ThumbLoader {
    private val cache = object : LruCache<String, Bitmap>(24 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }
    private val pool = Executors.newFixedThreadPool(3)
    private val main = Handler(Looper.getMainLooper())

    fun load(context: Context, url: String, into: ImageView) {
        into.tag = url
        cache.get(url)?.let { into.setImageBitmap(it); return }
        into.setImageDrawable(null)
        val app = context.applicationContext
        pool.execute {
            val bmp = try { decode(app, url) } catch (_: Exception) { null }
            if (bmp != null) cache.put(url, bmp)
            main.post { if (into.tag == url && bmp != null) into.setImageBitmap(bmp) }
        }
    }

    private fun decode(ctx: Context, url: String): Bitmap? {
        val bytes = if (url.startsWith("asset:")) {
            ctx.assets.open(url.removePrefix("asset:")).use { it.readBytes() }
        } else {
            WallpaperRepository.http.newCall(Request.Builder().url(url).build()).execute().use { r ->
                if (!r.isSuccessful) return null
                r.body?.bytes() ?: return null
            }
        }
        val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, o)
        var sample = 1
        while (o.outWidth / sample > 720) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    }
}
