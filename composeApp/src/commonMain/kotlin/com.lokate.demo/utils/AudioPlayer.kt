package com.lokate.demo.utils

interface AudioPlayer{
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Int)
    fun setDataSource(source: String)
    fun getDuration(): Int
    fun getCurrentPosition(): Int

    val isRunning: Boolean
}
expect fun getAudioPlayer(): AudioPlayer

