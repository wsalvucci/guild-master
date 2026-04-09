package com.example.demo.db.world

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class WorldDatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(dbFileName: String): SqlDriver =
        AndroidSqliteDriver(WorldDatabase.Schema, context, dbFileName)
}