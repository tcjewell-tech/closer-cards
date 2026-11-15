package com.tom.closercards.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily

private val LocalCloserColors = staticCompositionLocalOf { LightCloserColors }
private val LocalCloserTypography = staticCompositionLocalOf {
    closerTypography(FontFamily.Serif, FontFamily.SansSerif)
}

/** Follows the system theme (MVP has no toggle). Dark is the expected setting on a date. */
@Composable
fun CloserCardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkCloserColors else LightCloserColors
    val typography = remember { closerTypography(Newsreader, Figtree) }
    CompositionLocalProvider(
        LocalCloserColors provides colors,
        LocalCloserTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = colors.toColorScheme(darkTheme),
            typography = typography.toMaterial(),
            shapes = Shapes(
                small = CloserShapes.small,
                medium = CloserShapes.small,
                large = CloserShapes.card,
            ),
            content = content,
        )
    }
}

/** Access to the system's own tokens, for anything Material 3 slots don't cover. */
object CloserTheme {
    val colors: CloserColors
        @Composable @ReadOnlyComposable
        get() = LocalCloserColors.current

    val typography: CloserTypography
        @Composable @ReadOnlyComposable
        get() = LocalCloserTypography.current
}

private fun CloserColors.toColorScheme(dark: Boolean): ColorScheme {
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        secondary = set2,
        onSecondary = onSet2,
        tertiary = set3,
        onTertiary = onSet3,
        background = surface,
        onBackground = ink,
        surface = surface,
        onSurface = ink,
        surfaceVariant = surfaceRaised,
        onSurfaceVariant = inkMuted,
        surfaceContainerLowest = surfaceRaised,
        surfaceContainerLow = surfaceRaised,
        surfaceContainer = surfaceRaised,
        surfaceContainerHigh = surfaceRaised,
        surfaceContainerHighest = surfaceRaised,
        outline = outline,
        outlineVariant = hairline,
    )
}
