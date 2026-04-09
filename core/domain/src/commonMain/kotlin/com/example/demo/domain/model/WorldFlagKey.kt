package com.example.demo.domain.model

sealed class WorldFlagKey(val raw: String) {
    data object NewPlayerOnboarding : WorldFlagKey("new_player_onboarding")
    data class Unknown(val value: String) : WorldFlagKey(value)

    companion object P
    fun fromRaw(raw: String): WorldFlagKey = when (raw) {
        NewPlayerOnboarding.raw -> NewPlayerOnboarding
        else -> Unknown(raw)
    }
}
