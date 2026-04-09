package com.example.demo.db.world

expect class WorldDatabaseFileManager {
    suspend fun deleteDatabase(id: Long)
}