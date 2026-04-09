package com.example.demo.domain.repository

import com.example.demo.domain.model.worldsave.WorldSave

interface CloudSaveSyncService {
    suspend fun uploadSaveBundle(saveId: Long)
    suspend fun downloadSaveBundle(saveId: Long)
    suspend fun listRemoteSaves(): List<WorldSave>
}