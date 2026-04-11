package com.example.demo.domain.repository

import com.example.demo.domain.model.worldsave.CharacterMeta
import com.example.demo.domain.model.worldsave.WorldSave


interface WorldSaveRepository {
    suspend fun open(saveId: Long)
    suspend fun close()

    suspend fun save(worldSave: WorldSave, saveId: Long)
    suspend fun load(saveId: Long): WorldSave

    suspend fun initializeNewWorld(saveId: Long)
    suspend fun deleteWorldFile(saveId: Long)

    suspend fun createNewCharacterMeta(
        characterUuid: String,
        name: String
    )
}