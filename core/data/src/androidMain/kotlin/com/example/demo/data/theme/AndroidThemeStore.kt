package com.example.demo.data.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

private object ThemeKeys {
    val THEME_ID = stringPreferencesKey("theme_id")
    val THEME_MODE = stringPreferencesKey("theme_mode")
}

class AndroidThemeStore(
    private val context: Context
) : PlatformThemeStore {
    override val prefs: Flow<ThemePrefs> =
        context.themeDataStore.data.map { prefs ->
            ThemePrefs(
                themeId = prefs[ThemeKeys.THEME_ID] ?: "DEFAULT",
                themeMode = prefs[ThemeKeys.THEME_MODE] ?: "SYSTEM",
            )
        }

    override suspend fun setThemeId(themeId: String) {
        context.themeDataStore.edit { prefs ->
            prefs[ThemeKeys.THEME_ID] = themeId
        }
    }

    override suspend fun setThemeMode(mode: String) {
        context.themeDataStore.edit { prefs ->
            prefs[ThemeKeys.THEME_MODE] = mode
        }
    }
}