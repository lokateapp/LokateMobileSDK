package com.lokate.kmmsdk.data.datasource.local.factory

import app.cash.sqldelight.db.SqlDriver
import com.lokate.kmmsdk.Database

expect class DriverFactory{
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database =
    Database(driverFactory.createDriver())
