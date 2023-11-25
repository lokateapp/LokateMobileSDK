package com.lokate.kmmsdk.data.datasource.local.factory

import app.cash.sqldelight.db.SqlDriver
import com.lokate.Database

expect class DriverFactory{
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    val database = Database(driver)
    return database
}