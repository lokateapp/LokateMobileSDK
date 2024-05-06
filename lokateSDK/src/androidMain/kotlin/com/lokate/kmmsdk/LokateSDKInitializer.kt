package com.lokate.kmmsdk

import android.content.Context
import androidx.startup.Initializer

lateinit var applicationContext: Context
    private set

object LokateSDKContext

class LokateSDKInitializer : Initializer<LokateSDKContext> {
    override fun create(context: Context): LokateSDKContext {
        applicationContext = context.applicationContext
        return LokateSDKContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf()
    }
}
