package com.example.demo.domain.usecases

import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.repository.WorldSaveRepository

class DeleteGuildSaveFileUseCase(
    private val saveSlotRepository: SaveSlotRepository,
    private val worldSaveRepository: WorldSaveRepository
) {
    suspend operator fun invoke(saveId: Long) {
        worldSaveRepository.deleteWorldFile(saveId)
        saveSlotRepository.deleteSaveSlot(saveId)
    }
}