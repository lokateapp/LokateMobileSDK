package com.lokate.kmmobilesdk

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name} and faruk!"
    }
}