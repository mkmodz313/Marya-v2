package com.myra.assistant.voice

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import com.myra.assistant.utils.Constants
import com.myra.assistant.utils.Logger
import java.util.concurrent.ConcurrentLinkedQueue

class AudioPlayer {
    private val TAG = "PLAYER"
    private val SAMPLE_RATE = Constants.SAMPLE_RATE_OUT

    private var audioTrack: AudioTrack? = null
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    @Volatile private var isPlaying = false
    private var playThread: Thread? = null

    /** Total PCM frames handed to the AudioTrack so far (used to time-align lip-sync with the speaker). */
    private var framesWritten = 0L

    var onPlaybackStarted: (() -> Unit)? = null
    var onPlaybackFinished: (() -> Unit)? = null

    init {
        initTrack()
        startPlayThread()
    }

    private fun initTrack() {
        val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf * 4)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } else {
            @Suppress("DEPRECATION")
            AudioTrack(
                android.media.AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBuf * 4,
                AudioTrack.MODE_STREAM
            )
        }
        audioTrack?.play()
        attachLipSync()
    }

    /**
     * Lets [LipSyncBus] ask "which frame is the speaker playing right now?" so the avatar's mouth is
     * aligned with the audible voice rather than with the (earlier) moment the chunk was queued.
     */
    private fun attachLipSync() {
        LipSyncBus.reset()
        LipSyncBus.playedFramesProvider = {
            // playbackHeadPosition is an unsigned 32-bit frame counter
            (audioTrack?.playbackHeadPosition?.toLong() ?: 0L) and 0xFFFFFFFFL
        }
    }

    private fun startPlayThread() {
        playThread = Thread {
            var wasPlaying = false
            while (!Thread.interrupted()) {
                val chunk = queue.poll()
                if (chunk != null) {
                    if (!wasPlaying) {
                        wasPlaying = true
                        VoiceStateManager.setSpeaking()
                        onPlaybackStarted?.invoke()
                    }
                    try {
                        // Feed the very same PCM to the lip-sync analyser (cheap, never throws).
                        LipSyncBus.onPcm(chunk, framesWritten)
                        framesWritten += chunk.size / 2
                        audioTrack?.write(chunk, 0, chunk.size)
                    } catch (e: Exception) {
                        Logger.e(TAG, "Write error: ${e.message}")
                    }
                } else {
                    if (wasPlaying) {
                        wasPlaying = false
                        onPlaybackFinished?.invoke()
                    }
                    Thread.sleep(10)
                }
            }
        }.also {
            it.name = "MYRA_PlayThread"
            it.isDaemon = true
            it.start()
        }
    }

    fun playChunk(data: ByteArray) {
        if (data.isNotEmpty()) queue.offer(data)
    }

    fun clearAndStop() {
        queue.clear()
        VoiceStateManager.setListening()
    }

    fun release() {
        LipSyncBus.playedFramesProvider = null
        LipSyncBus.reset()
        playThread?.interrupt()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        queue.clear()
    }
}
