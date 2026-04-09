package com.example.demo

import kotlinx.serialization.Serializable

@Serializable
data object GuildPickerRoute

@Serializable
data class SavePickerRoute(val guildId: Long)

@Serializable
data class MainGameRoute(val guildId: Long, val saveId: Long)

