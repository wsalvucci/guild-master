package com.example.demo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.theme.ThemePreferences

@Composable
fun PreviewThemeController(
    prefs: ThemePreferences = ThemePreferences(
        themeId = AppThemeId.DEFAULT,
        mode = ThemeMode.SYSTEM
    ),
    content: @Composable () -> Unit
) {
    val controller = ThemeController(
        prefs = prefs,
        setTheme = {},
        setMode = {}
    )
    DemoTheme(
        themeId = prefs.themeId,
        mode = prefs.mode
    ) {
        CompositionLocalProvider(LocalThemeController provides controller) {
            content()
        }
    }
}