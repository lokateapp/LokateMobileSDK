package com.lokate.kmmsdk.data.datasource.local.factory

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.lokate.kmmsdk.Database
import com.lokate.kmmsdk.applicationContext

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "test.db")
    }
}

actual fun getDatabase(): Database = createDatabase(AndroidDatabaseDriverFactory(applicationContext))
