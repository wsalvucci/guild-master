package com.example.demo.domain.model.worldsave

import com.example.demo.domain.model.Guild
import com.example.demo.domain.model.tasks.Task

data class WorldSave(
    val header: WorldSaveHeader,
    val flags: List<WorldFlag>,
    val characterMeta: CharacterMeta?,
    val characterData: CharacterData,
    val activeTasks: List<Task>,
    val guild: Guild
)

data class WorldSaveHeader(
    val saveId: Long,
    val timestamp: Long,
    val playtime: Long
)

data class WorldFlag(
    val id: Long,
    val key: String,
    val enabled: Boolean,
)
