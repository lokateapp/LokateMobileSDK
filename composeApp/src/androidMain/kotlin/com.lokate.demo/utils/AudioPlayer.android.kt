package com.lokate.demo.utils

import android.content.res.AssetManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toFile
import com.lokate.kmmsdk.applicationContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.ResourceItem
import org.lighthousegames.logging.Log
import java.io.File
import java.io.FileDescriptor

private object AndroidResourceReader

class AndroidAudioPlayer : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    override fun play() {
        if (mediaPlayer == null) {
            Log.e("AudioPlayer", "mediaPlayer is null")
            return
        }
        mediaPlayer?.start()
    }

    override fun pause() {
        if (mediaPlayer == null) {
            Log.e("AudioPlayer", "mediaPlayer is null")
            return
        }
        mediaPlayer?.pause()
    }

    override fun stop() {
        if (mediaPlayer == null) {
            Log.e("AudioPlayer", "mediaPlayer is null")
            return
        }
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    override fun seekTo(position: Int) {
        if (mediaPlayer == null) {
            Log.e("AudioPlayer", "mediaPlayer is null")
            return
        }
        mediaPlayer?.seekTo(position)
    }

    @OptIn(ExperimentalResourceApi::class)
    override fun setDataSource(source: String) {
        val assetManager = applicationContext.assets
        val afd = assetManager.openFd(source)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
        }
    }

    override fun getDuration(): Int {
        if (mediaPlayer == null) {
            Log.e("AudioPlayer", "mediaPlayer is null")
            return 0
        }
        return mediaPlayer?.duration ?: 0
    }

    override fun getCurrentPosition(): Int {
        if (mediaPlayer == null) {
            Log.e("AudioPlayer", "mediaPlayer is null")
            return 0
        }
        return mediaPlayer?.currentPosition ?: 0
    }

    override val isRunning: Boolean
          get() = mediaPlayer?.isPlaying == true
}

actual fun getAudioPlayer(): AudioPlayer {
    return AndroidAudioPlayer()
}