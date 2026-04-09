package com.example.demo.data.theme

import kotlinx.coroutines.flow.Flow

interface PlatformThemeStore {
    val prefs: Flow<ThemePrefs>
    suspend fun setThemeId(themeId: String)
    suspend fun setThemeMode(mode: String)
}

data class ThemePrefs(
    val themeId: String = "DEFAULT",
    val themeMode: String = "SYSTEM"
)
