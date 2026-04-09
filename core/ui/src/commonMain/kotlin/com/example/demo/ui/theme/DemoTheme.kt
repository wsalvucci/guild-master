package com.example.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode

@Composable
fun DemoTheme(
    themeId: AppThemeId,
    mode: ThemeMode,
    content: @Composable () -> Unit
) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val palette = paletteFor(themeId)
    MaterialTheme(
        colorScheme = if (dark) palette.dark else palette.light,
        content = content,
        typography = appTypography()
    )
}