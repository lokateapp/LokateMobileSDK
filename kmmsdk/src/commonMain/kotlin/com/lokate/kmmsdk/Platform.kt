package com.lokate.kmmsdk

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform