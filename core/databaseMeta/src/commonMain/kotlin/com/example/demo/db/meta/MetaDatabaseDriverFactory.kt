package com.example.demo.db.meta

import app.cash.sqldelight.db.SqlDriver

expect class MetaDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}