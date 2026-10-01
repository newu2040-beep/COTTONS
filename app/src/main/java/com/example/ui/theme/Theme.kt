package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun CottonsTheme(
    colors: CottonsColors = CottonsThemes.PicnicRed,
    darkMode: String = "SYSTEM",
    fontFamily: CottonsFontFamily = CottonsFontFamily.CURSIVE,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (darkMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemDark
    }

    val effectiveColors = if (isDark && !colors.isDark) colors.toDark() else colors

    val m3ColorScheme = if (isDark) {
        darkColorScheme(
            primary = effectiveColors.primary,
            secondary = effectiveColors.secondary,
            tertiary = effectiveColors.tertiary,
            background = effectiveColors.background,
            surface = effectiveColors.paper,
            onSurface = effectiveColors.ink,
            onBackground = effectiveColors.ink,
            onPrimary = effectiveColors.paper,
            onSecondary = effectiveColors.paper,
            onTertiary = effectiveColors.ink,
            outline = effectiveColors.inkSoft.copy(alpha = 0.5f),
            surfaceVariant = effectiveColors.paperAlt
        )
    } else {
        lightColorScheme(
            primary = effectiveColors.primary,
            secondary = effectiveColors.secondary,
            tertiary = effectiveColors.tertiary,
            background = effectiveColors.background,
            surface = effectiveColors.paper,
            onSurface = effectiveColors.ink,
            onBackground = effectiveColors.ink,
            onPrimary = effectiveColors.paper,
            onSecondary = effectiveColors.paper,
            onTertiary = effectiveColors.ink,
            outline = effectiveColors.inkSoft.copy(alpha = 0.4f),
            surfaceVariant = effectiveColors.paperAlt
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !isDark
                controller.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    val dynamicTypography = getCottonsTypography(fontFamily.font)

    CompositionLocalProvider(
        LocalCottons provides effectiveColors,
        LocalCottonsFont provides fontFamily
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            typography = dynamicTypography,
            content = content
        )
    }
}
