package com.example.demo.domain.usecases

import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.repository.WorldSaveRepository

class SaveGuildFileUseCase(
    private val worldSaveRepository: WorldSaveRepository,
    private val saveSlotRepository: SaveSlotRepository
) {
    suspend operator fun invoke(worldSave: WorldSave, targetSaveId: Long) {
        worldSaveRepository.save(worldSave, targetSaveId)
        saveSlotRepository.markSaved(
            saveId = targetSaveId,
            savedAtEpochMs = worldSave.header.timestamp,
            playtimeSeconds = worldSave.header.playtime
        )
    }
}