package com.lokate.kmmsdk.data.datasource.local

import com.russhwolf.settings.Settings

object Settings {
    val authenticationSettings: Settings =
        Settings().apply {
            this.putString("auth_token", "")
        }
}
