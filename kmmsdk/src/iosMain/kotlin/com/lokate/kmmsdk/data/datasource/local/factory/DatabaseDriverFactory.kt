package com.lokate.kmmsdk.data.datasource.local.factory

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.lokate.kmmsdk.Database

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "test.db")
    }
}
