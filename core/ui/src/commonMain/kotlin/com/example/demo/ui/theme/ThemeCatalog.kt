package com.example.demo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.demo.domain.theme.AppThemeId

data class Palette(
    val light: ColorScheme,
    val dark: ColorScheme,
)

fun paletteFor(theme: AppThemeId): Palette = when (theme) {
    AppThemeId.DEFAULT -> Palette(
        light = lightColorScheme(
            primary = Color(0xFFFFB4A8),
            onPrimary = Color(0xFF690000),
            primaryContainer = Color(0xFF990000),
            onPrimaryContainer = Color(0xFFFFA092),
            inversePrimary = Color(0xFFB82014),
            secondary = Color(0xFFFFF9EF),
            onSecondary = Color(0xFF3A3000),
            secondaryContainer = Color(0xFFFFDB3C),
            onSecondaryContainer = Color(0xFF725F00),
            tertiary = Color(0xFF8FD87D),
            onTertiary = Color(0xFF003A00),
            tertiaryContainer = Color(0xFF0E5608),
            onTertiaryContainer = Color(0xFF83CB71),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF131313),
            onBackground = Color(0xFFE5E2E1),
            surface = Color(0xFF131313),
            onSurface = Color(0xFFE5E2E1),
            surfaceVariant = Color(0xFF353535),
            onSurfaceVariant = Color(0xFFE4BEB8),
            outline = Color(0xFFAB8983),
            outlineVariant = Color(0xFF5B403C),
            inverseSurface = Color(0xFFE5E2E1),
            inverseOnSurface = Color(0xFF313030),
            surfaceTint = Color(0xFFFFB4A8),
            surfaceBright = Color(0xFF393939),
            surfaceDim = Color(0xFF131313),
            surfaceContainerLowest = Color(0xFF0E0E0E),
            surfaceContainerLow = Color(0xFF1C1B1B),
            surfaceContainer = Color(0xFF20201F),
            surfaceContainerHigh = Color(0xFF2A2A2A),
            surfaceContainerHighest = Color(0xFF353535),
            primaryFixed = Color(0xFFFFDAD4),
            onPrimaryFixed = Color(0xFF410000),
            primaryFixedDim = Color(0xFFFFB4A8),
            onPrimaryFixedVariant = Color(0xFF930000),
            secondaryFixed = Color(0xFFFFE16D),
            onSecondaryFixed = Color(0xFF221B00),
            secondaryFixedDim = Color(0xFFE9C400),
            onSecondaryFixedVariant = Color(0xFF544600),
            tertiaryFixed = Color(0xFFAAF596),
            onTertiaryFixed = Color(0xFF002200),
            tertiaryFixedDim = Color(0xFF8FD87D),
            onTertiaryFixedVariant = Color(0xFF095305),
        ),
        dark = darkColorScheme(
            primary = Color(0xFFFFB4A8),
            onPrimary = Color(0xFF690000),
            primaryContainer = Color(0xFF990000),
            onPrimaryContainer = Color(0xFFFFA092),
            inversePrimary = Color(0xFFB82014),
            secondary = Color(0xFFFFF9EF),
            onSecondary = Color(0xFF3A3000),
            secondaryContainer = Color(0xFFFFDB3C),
            onSecondaryContainer = Color(0xFF725F00),
            tertiary = Color(0xFF8FD87D),
            onTertiary = Color(0xFF003A00),
            tertiaryContainer = Color(0xFF0E5608),
            onTertiaryContainer = Color(0xFF83CB71),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF131313),
            onBackground = Color(0xFFE5E2E1),
            surface = Color(0xFF131313),
            onSurface = Color(0xFFE5E2E1),
            surfaceVariant = Color(0xFF353535),
            onSurfaceVariant = Color(0xFFE4BEB8),
            outline = Color(0xFFAB8983),
            outlineVariant = Color(0xFF5B403C),
            inverseSurface = Color(0xFFE5E2E1),
            inverseOnSurface = Color(0xFF313030),
            surfaceTint = Color(0xFFFFB4A8),
            surfaceBright = Color(0xFF393939),
            surfaceDim = Color(0xFF131313),
            surfaceContainerLowest = Color(0xFF0E0E0E),
            surfaceContainerLow = Color(0xFF1C1B1B),
            surfaceContainer = Color(0xFF20201F),
            surfaceContainerHigh = Color(0xFF2A2A2A),
            surfaceContainerHighest = Color(0xFF353535),
            primaryFixed = Color(0xFFFFDAD4),
            onPrimaryFixed = Color(0xFF410000),
            primaryFixedDim = Color(0xFFFFB4A8),
            onPrimaryFixedVariant = Color(0xFF930000),
            secondaryFixed = Color(0xFFFFE16D),
            onSecondaryFixed = Color(0xFF221B00),
            secondaryFixedDim = Color(0xFFE9C400),
            onSecondaryFixedVariant = Color(0xFF544600),
            tertiaryFixed = Color(0xFFAAF596),
            onTertiaryFixed = Color(0xFF002200),
            tertiaryFixedDim = Color(0xFF8FD87D),
            onTertiaryFixedVariant = Color(0xFF095305),
        )
    )

    AppThemeId.OCEAN -> Palette(
        light = lightColorScheme(
            primary = Color(0xFF006494),
            secondary = Color(0xFF00A6A6),
        ),
        dark = darkColorScheme(
            primary = Color(0xFF64C7FF),
            secondary = Color(0xFF5CE1E6),
        )
    )

    AppThemeId.FOREST -> Palette(
        light = lightColorScheme(
            primary = Color(0xFF2E7D32)
        ),
        dark = darkColorScheme(
            primary = Color(0xFF81C784)
        )
    )

    AppThemeId.RETRO -> Palette(
        light = lightColorScheme(
            primary = Color(0xFF8E24AA)
        ),
        dark = darkColorScheme(
            primary = Color(0xFFCE93D8)
        )
    )
}