package com.example.demo.domain.usecases

import com.example.demo.domain.repository.WorldSaveRepository

class OpenGuildSaveFileUseCase(
    private val worldSaveRepository: WorldSaveRepository
) {
    suspend operator fun invoke(saveId: Long) {
        worldSaveRepository.open(saveId)
    }
}