package com.example.demo.domain.model

data class WorldSaveSlot(
    val saveId: Long,
    val accountId: Long,
    val slot: Int,
    val characterName: String,
    val worldDbFile: String,
    val playtimeSeconds: Long,
    val lastSavedAt: Long
)
