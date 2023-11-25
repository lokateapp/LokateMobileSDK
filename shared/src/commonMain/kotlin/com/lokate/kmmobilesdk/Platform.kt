package com.lokate.kmmobilesdk

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform