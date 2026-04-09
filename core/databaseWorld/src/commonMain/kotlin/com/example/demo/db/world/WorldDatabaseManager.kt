package com.example.demo.db.world

interface WorldDatabaseManager {
    suspend fun open(saveId: Long)
    suspend fun close()
    fun requireDatabase(): WorldDatabase
    fun currentSaveId(): Long?
}