package com.tom.closercards.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF9C3D54),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DF),
    onPrimaryContainer = Color(0xFF3F0016),
    secondaryContainer = Color(0xFFF6DDE1),
    onSecondaryContainer = Color(0xFF2B1519),
    background = Color(0xFFFFF8F7),
    surface = Color(0xFFFFF8F7),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB1C1),
    onPrimary = Color(0xFF5F1127),
    primaryContainer = Color(0xFF7E2540),
    onPrimaryContainer = Color(0xFFFFD9DF),
    secondaryContainer = Color(0xFF5C3F44),
    onSecondaryContainer = Color(0xFFF6DDE1),
)

// The adult decks always use their own dark palette so it's obvious which deck is on screen.
private val AfterDarkColors = darkColorScheme(
    primary = Color(0xFFF2A7C3),
    onPrimary = Color(0xFF4A0E2C),
    primaryContainer = Color(0xFF5E1B3C),
    onPrimaryContainer = Color(0xFFFFD8E8),
    secondaryContainer = Color(0xFF3D2333),
    onSecondaryContainer = Color(0xFFF5DCEA),
    background = Color(0xFF140A10),
    onBackground = Color(0xFFF3E3EA),
    surface = Color(0xFF140A10),
    onSurface = Color(0xFFF3E3EA),
    surfaceContainerHigh = Color(0xFF2A1822),
)

private val AfterDarkerColors = darkColorScheme(
    primary = Color(0xFFFF8A8F),
    onPrimary = Color(0xFF5C0A12),
    primaryContainer = Color(0xFF6B0F1A),
    onPrimaryContainer = Color(0xFFFFDADB),
    secondaryContainer = Color(0xFF3A1E21),
    onSecondaryContainer = Color(0xFFF7DCDD),
    background = Color(0xFF0B0708),
    onBackground = Color(0xFFF1E1E2),
    surface = Color(0xFF0B0708),
    onSurface = Color(0xFFF1E1E2),
    surfaceContainerHigh = Color(0xFF241618),
)

/** True when [deckId] forces a dark palette regardless of the system setting. */
fun isAdultDeck(deckId: String) = deckId == "after-dark" || deckId == "after-darker"

/** The main deck follows the system light/dark setting; there is no in-app toggle in v1. */
@Composable
fun CloserCardsTheme(deckId: String = "main", content: @Composable () -> Unit) {
    val colors = when (deckId) {
        "after-dark" -> AfterDarkColors
        "after-darker" -> AfterDarkerColors
        else -> if (isSystemInDarkTheme()) DarkColors else LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}

/** Centered, scrollable screen body, so large system font sizes never clip content. */
@Composable
fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}
