package com.example.demo.domain.usecases

import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.repository.WorldSaveRepository

class AutoSaveGuildFileUseCase(
    private val worldSaveRepository: WorldSaveRepository,
    private val saveSlotRepository: SaveSlotRepository
) {
    suspend operator fun invoke(worldSave: WorldSave) {
        worldSaveRepository.save(worldSave, worldSave.header.saveId)
        saveSlotRepository.markSaved(
            saveId = worldSave.header.saveId,
            savedAtEpochMs = worldSave.header.timestamp,
            playtimeSeconds = worldSave.header.playtime
        )
    }
}