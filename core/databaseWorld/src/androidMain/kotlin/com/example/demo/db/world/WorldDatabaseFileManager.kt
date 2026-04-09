package com.example.demo.db.world

import android.content.Context

actual class WorldDatabaseFileManager(private val context: Context) {
    actual suspend fun deleteDatabase(id: Long) {
        context.deleteDatabase("save_${id}.db")
    }
}