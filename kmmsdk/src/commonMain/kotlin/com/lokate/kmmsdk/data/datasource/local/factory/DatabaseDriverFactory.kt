package com.lokate.kmmsdk.data.datasource.local.factory

import app.cash.sqldelight.db.SqlDriver
import com.lokate.kmmsdk.Database

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(databaseDriverFactory: DatabaseDriverFactory) = Database(databaseDriverFactory.createDriver())

expect fun getDatabase(): Database
