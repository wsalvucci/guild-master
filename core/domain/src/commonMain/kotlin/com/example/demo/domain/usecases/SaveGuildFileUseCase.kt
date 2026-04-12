package com.example.demo.domain.usecases

import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.repository.WorldSaveRepository
import kotlin.time.Clock

class SaveGuildFileUseCase(
    private val worldSaveRepository: WorldSaveRepository,
    private val saveSlotRepository: SaveSlotRepository
) {
    suspend operator fun invoke(worldSave: WorldSave, targetSaveId: Long) {
        worldSaveRepository.save(worldSave, targetSaveId)
        saveSlotRepository.markSaved(
            saveId = targetSaveId,
            savedAtEpoch = Clock.System.now().epochSeconds,
            playtimeSeconds = worldSave.header.playtime
        )
    }
}