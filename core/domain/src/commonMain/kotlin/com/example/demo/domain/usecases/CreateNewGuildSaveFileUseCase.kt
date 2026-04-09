package com.example.demo.domain.usecases

import com.example.demo.domain.model.WorldSaveSlot
import com.example.demo.domain.repository.SaveSlotRepository

class CreateNewGuildSaveFileUseCase(
    private val saveSlotRepository: SaveSlotRepository
) {
    suspend operator fun invoke(
        accountId: Long,
        slot: Long,
        guildName: String
    ) : WorldSaveSlot {
        return saveSlotRepository.createSaveSlot(
            accountId = accountId,
            slot = slot,
            name = guildName
        )
    }
}