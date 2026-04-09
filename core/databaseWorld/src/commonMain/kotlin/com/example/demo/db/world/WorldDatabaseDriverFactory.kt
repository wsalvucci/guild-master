package com.example.demo.db.world

import app.cash.sqldelight.db.SqlDriver

expect class WorldDatabaseDriverFactory {
    fun createDriver(dbFileName: String): SqlDriver
}