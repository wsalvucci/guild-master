package com.example.demo.domain.usecases

import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.repository.WorldSaveRepository

class LoadGuildSaveFileUseCase(
    private val worldSaveRepository: WorldSaveRepository
) {
    suspend operator fun invoke(saveId: Long) : WorldSave {
        return worldSaveRepository.load(saveId)
    }
}