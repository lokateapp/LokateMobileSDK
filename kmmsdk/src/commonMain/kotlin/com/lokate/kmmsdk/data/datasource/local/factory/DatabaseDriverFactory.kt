package com.lokate.kmmsdk.data.datasource.local.factory

import app.cash.sqldelight.db.SqlDriver
import com.lokate.kmmsdk.Database
import com.lokate.kmmsdk.data.datasource.local.factory.DriverFactory

fun createDatabase(driverFactory: DriverFactory): Database =
    Database(driverFactory.createDriver())
