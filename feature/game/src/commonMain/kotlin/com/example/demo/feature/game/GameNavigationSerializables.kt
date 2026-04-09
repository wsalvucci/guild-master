package com.example.demo.feature.game

import kotlinx.serialization.Serializable

@Serializable
data class SaveListRoute(val guildId: Long)

@Serializable
data class GameRoute(val guildId: Long, val saveId: Long)
