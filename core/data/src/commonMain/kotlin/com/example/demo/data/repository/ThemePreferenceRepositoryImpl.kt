package com.example.demo.data.repository

import com.example.demo.data.theme.PlatformThemeStore
import com.example.demo.domain.repository.ThemePreferenceRepository
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.theme.ThemePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferenceRepositoryImpl(
    private val store: PlatformThemeStore
) : ThemePreferenceRepository {
    override val preferences: Flow<ThemePreferences> =
        store.prefs.map { raw ->
            ThemePreferences(
                themeId = raw.themeId.toAppThemeIdOrDefault(),
                mode = raw.themeMode.toThemeModeOrDefault()
            )
        }

    override suspend fun setThemeId(id: AppThemeId) {
        store.setThemeId(id.name)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        store.setThemeMode(mode.name)
    }

    private fun String.toAppThemeIdOrDefault(): AppThemeId =
        runCatching { AppThemeId.valueOf(this) }.getOrDefault(AppThemeId.DEFAULT)

    private fun String.toThemeModeOrDefault(): ThemeMode =
        runCatching { ThemeMode.valueOf(this) }.getOrDefault(ThemeMode.SYSTEM)
}