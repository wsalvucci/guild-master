package com.example.demo.domain.usecases

import com.example.demo.domain.model.worldsave.CharacterMeta
import com.example.demo.domain.repository.WorldSaveRepository

class CreateNewCharacterMetaUseCase(
    private val worldSaveRepository: WorldSaveRepository
) {
    suspend operator fun invoke(name: String) {
        worldSaveRepository.createNewCharacterMeta(name)
    }
}