package com.example.demo.domain.theme

enum class AppThemeId {
    DEFAULT,
    OCEAN,
    FOREST,
    RETRO
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class ThemePreferences(
    val themeId: AppThemeId = AppThemeId.DEFAULT,
    val mode: ThemeMode = ThemeMode.SYSTEM
)