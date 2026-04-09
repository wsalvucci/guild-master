package com.example.demo.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.theme.ThemePreferences

data class ThemeController(
    val prefs: ThemePreferences,
    val setTheme: (AppThemeId) -> Unit,
    val setMode: (ThemeMode) -> Unit
)

val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    error("No LocalThemeController provided")
}
