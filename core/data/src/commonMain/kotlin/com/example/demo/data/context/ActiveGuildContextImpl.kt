package com.example.demo.data.context

import com.example.demo.domain.ActiveGuildContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActiveGuildContextImpl : ActiveGuildContext {
    private val _guildId: MutableStateFlow<Long?> = MutableStateFlow(null)
    override val guildId: StateFlow<Long?> = _guildId.asStateFlow()

    override fun setGuildId(id: Long) {
        _guildId.value = id
    }

    override fun clear() {
        _guildId.value = null
    }
}