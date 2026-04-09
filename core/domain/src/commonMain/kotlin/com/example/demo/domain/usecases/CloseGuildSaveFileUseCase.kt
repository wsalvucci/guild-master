package com.example.demo.domain.usecases

import com.example.demo.domain.repository.WorldSaveRepository

class CloseGuildSaveFileUseCase(
    private val worldSaveRepository: WorldSaveRepository
) {
    suspend operator fun invoke() {
        worldSaveRepository.close()
    }
}