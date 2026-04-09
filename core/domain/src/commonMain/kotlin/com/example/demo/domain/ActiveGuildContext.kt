package com.example.demo.domain

import kotlinx.coroutines.flow.StateFlow

interface ActiveGuildContext {
    val guildId: StateFlow<Long?>
    fun setGuildId(id: Long)
    fun clear()
}