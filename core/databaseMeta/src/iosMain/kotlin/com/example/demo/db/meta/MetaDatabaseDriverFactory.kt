package com.example.demo.db.meta

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class MetaDatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(MetaDatabase.Schema, "meta.db")
}