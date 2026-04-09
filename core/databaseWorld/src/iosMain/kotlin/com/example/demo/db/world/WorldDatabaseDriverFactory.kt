package com.example.demo.db.world

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class WorldDatabaseDriverFactory {
    actual fun createDriver(dbFileName: String): SqlDriver =
        NativeSqliteDriver(WorldDatabase.Schema, dbFileName)
}