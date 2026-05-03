package com.example.jadwalkuliah.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = GoldAccent,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    tertiary = md_theme_light_primary,
    onTertiary = md_theme_light_onTertiary,
    outline = md_theme_light_outline
)

private val PurpleColorScheme = darkColorScheme(
    primary = PurplePrimary,
    secondary = PurpleSecondary,
    tertiary = PurpleTertiary,
    background = PurpleBackground,
    surface = PurpleSurface,
    onPrimary = Color(0xFF381E72),
    onSecondary = Color(0xFF332D41),
    onTertiary = Color(0xFF492532),
    onBackground = PurpleText,
    onSurface = PurpleText,
    outline = Color(0xFF938F99)
)

private val PinkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    secondary = PinkSecondary,
    tertiary = PinkTertiary,
    background = PinkBackground,
    surface = PinkSurface,
    onPrimary = Color(0xFF5E1129),
    onSecondary = Color(0xFF422930),
    onTertiary = Color(0xFF5E1231),
    onBackground = PinkText,
    onSurface = PinkText,
    outline = Color(0xFF9F8C8F)
)

@Composable
fun JadwalKuliahTheme(
    darkTheme: Boolean = true,
    themeMode: String = "Dark",
    content: @Composable () -> Unit
) {
    // Prioritaskan themeMode, gunakan darkTheme sebagai fallback jika themeMode default ("Dark")
    val effectiveTheme = if (themeMode == "Dark" && !darkTheme) "Yellow" else themeMode
    
    val colorScheme = when (effectiveTheme) {
        "Yellow" -> LightColorScheme
        "Purple" -> PurpleColorScheme
        "Pink" -> PinkColorScheme
        else -> DarkColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = themeMode == "Yellow"
        }
    }

    val shapes = androidx.compose.material3.Shapes(
        extraSmall = RoundedCornerShape(12.dp),
        small = RoundedCornerShape(16.dp),
        medium = RoundedCornerShape(24.dp),
        large = RoundedCornerShape(30.dp),
        extraLarge = RoundedCornerShape(32.dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
