package com.example.demo.domain.usecases

import com.example.demo.domain.model.WorldSaveSlot
import com.example.demo.domain.repository.SaveSlotRepository

class GetGuildSaveFilesUseCase(
    private val saveSlotRepository: SaveSlotRepository
) {
    suspend operator fun invoke(guildId: Long): List<WorldSaveSlot> {
        return saveSlotRepository.listSaveSlots(accountId = guildId)
    }
}