package com.example.demo.domain.repository

import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.theme.ThemePreferences
import kotlinx.coroutines.flow.Flow

interface ThemePreferenceRepository {
    val preferences: Flow<ThemePreferences>
    suspend fun setThemeId(id: AppThemeId)
    suspend fun setThemeMode(mode: ThemeMode)
}