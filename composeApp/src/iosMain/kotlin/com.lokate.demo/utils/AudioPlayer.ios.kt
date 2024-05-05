package com.lokate.demo.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle
import platform.Foundation.NSLog
import platform.Foundation.NSURL

class IOSAudioPlayer : AudioPlayer {
    private var avPlayer: AVAudioPlayer? = null

    override fun play() {
        if (avPlayer == null) {
            NSLog("AudioPlayer", "mediaPlayer is null")
            return
        }
        avPlayer?.play()
    }

    override fun pause() {
        if (avPlayer == null) {
            NSLog("AudioPlayer", "mediaPlayer is null")
            return
        }
        avPlayer?.pause()
    }

    override fun stop() {
        if (avPlayer == null) {
            NSLog("AudioPlayer", "mediaPlayer is null")
            return
        }
        avPlayer?.stop()
    }

    override fun seekTo(position: Int) {
        if (avPlayer == null) {
            NSLog("AudioPlayer", "mediaPlayer is null")
            return
        }
        avPlayer?.currentTime = position.toDouble()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun setDataSource(source: String) {
        NSLog("AudioPlayer source: $source")
        val url =
            NSBundle.mainBundle.resourcePath()?.let {
                NSURL.fileURLWithPath("$it/compose-resources/$source")
            }
        NSLog("AudioPlayer url: $url")
        if (url == null) {
            NSLog("AudioPlayer", "url is null")
            return
        }
        avPlayer = AVAudioPlayer(url, null)
    }

    override fun getDuration(): Int {
        if (avPlayer == null) {
            NSLog("AudioPlayer", "mediaPlayer is null")
            return 0
        }
        return avPlayer?.duration?.toInt() ?: 0
    }

    override fun getCurrentPosition(): Int {
        if (avPlayer == null) {
            NSLog("AudioPlayer", "mediaPlayer is null")
            return 0
        }
        return avPlayer?.currentTime?.toInt() ?: 0
    }

    override val isRunning: Boolean
        get() = avPlayer?.isPlaying() == true
}

actual fun getAudioPlayer(): AudioPlayer {
    return IOSAudioPlayer()
}
