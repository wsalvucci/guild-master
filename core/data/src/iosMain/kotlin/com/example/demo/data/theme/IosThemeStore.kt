package com.example.demo.data.theme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

private const val THEME_ID_KEY = "theme_id"
private const val THEME_MODE_KEY = "theme_mode"

class IosThemeStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults()
) : PlatformThemeStore {
    private val state = MutableStateFlow(load())

    override val prefs: Flow<ThemePrefs> = state.asStateFlow()

    override suspend fun setThemeId(themeId: String) {
        defaults.setObject(themeId, forKey = THEME_ID_KEY)
        state.value = state.value.copy(themeId = themeId)
    }

    override suspend fun setThemeMode(mode: String) {
        defaults.setObject(mode, forKey = THEME_MODE_KEY)
        state.value = state.value.copy(themeMode = mode)
    }

    private fun load(): ThemePrefs {
        val themeId = defaults.stringForKey(THEME_ID_KEY) ?: "DEFAULT"
        val mode = defaults.stringForKey(THEME_MODE_KEY) ?: "SYSTEM"
        return ThemePrefs(themeId = themeId, themeMode = mode)
    }
}