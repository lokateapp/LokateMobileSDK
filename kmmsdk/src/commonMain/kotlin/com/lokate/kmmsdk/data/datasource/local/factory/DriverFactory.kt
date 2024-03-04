package com.lokate.kmmsdk.data.datasource.local.factory

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory{
    fun createDriver(): SqlDriver
}