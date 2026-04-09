package com.example.demo.domain.model.worldsave

import com.example.demo.domain.model.Guild

data class WorldSave(
    val header: WorldSaveHeader,
    val flags: List<WorldFlag>,
    val characterMeta: CharacterMeta?,
    val characterData: CharacterData,
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
