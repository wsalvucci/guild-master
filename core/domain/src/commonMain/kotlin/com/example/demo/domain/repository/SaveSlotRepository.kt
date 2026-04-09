package com.example.demo.domain.repository

import com.example.demo.domain.model.WorldSaveSlot

interface SaveSlotRepository {
    suspend fun createSaveSlot(accountId: Long, slot: Long, name: String): WorldSaveSlot
    suspend fun listSaveSlots(accountId: Long): List<WorldSaveSlot>
    suspend fun getSaveSlot(saveId: Long): WorldSaveSlot?
    suspend fun markSaved(saveId: Long, savedAtEpochMs: Long, playtimeSeconds: Long)
    suspend fun deleteSaveSlot(saveId: Long)
}