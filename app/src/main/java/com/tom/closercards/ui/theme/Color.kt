package com.tom.closercards.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colour tokens from the Closer Cards design system (tokens.json).
 * Change a value there first, then mirror it here.
 */
@Immutable
data class CloserColors(
    /** Screen background on every screen. */
    val surface: Color,
    /** Revealed card face, text fields, sheets. */
    val surfaceRaised: Color,
    /** Decorative dividers and the card face edge. Never the only boundary of a control. */
    val hairline: Color,
    /** Borders of controls: text fields, Skip, upcoming progress dots. */
    val outline: Color,
    /** Primary text on surface and surfaceRaised. */
    val ink: Color,
    /** Secondary text on surface and surfaceRaised. */
    val inkMuted: Color,
    /** The one main action per screen, the focus ring, done progress dots. */
    val primary: Color,
    val onPrimary: Color,
    /** Set 1: light and personal history. */
    val set1: Color,
    val onSet1: Color,
    /** Set 2: values and experiences. */
    val set2: Color,
    val onSet2: Color,
    /** Set 3: vulnerability and connection. Darkest of the three on purpose. */
    val set3: Color,
    val onSet3: Color,
) {
    /** Fill and content colour for set 1, 2 or 3. */
    fun forSet(set: Int): SetColors = when (set) {
        2 -> SetColors(set2, onSet2)
        3 -> SetColors(set3, onSet3)
        else -> SetColors(set1, onSet1)
    }
}

/** A set's fill and the colour for text and marks on it. */
@Immutable
data class SetColors(val container: Color, val content: Color)

val LightCloserColors = CloserColors(
    surface = Color(0xFFFAF5EE),
    surfaceRaised = Color(0xFFFFFDF9),
    hairline = Color(0xFFE6D9CD),
    outline = Color(0xFF8F7A7E),
    ink = Color(0xFF2A1A1F),
    inkMuted = Color(0xFF6B555B),
    primary = Color(0xFF8A2E4A),
    onPrimary = Color(0xFFFFFFFF),
    set1 = Color(0xFFECC07E),
    onSet1 = Color(0xFF2A1A1F),
    set2 = Color(0xFFB8553A),
    onSet2 = Color(0xFFFFFFFF),
    set3 = Color(0xFF5E1D38),
    onSet3 = Color(0xFFFFFFFF),
)

val DarkCloserColors = CloserColors(
    surface = Color(0xFF1A1315),
    surfaceRaised = Color(0xFF271D20),
    hairline = Color(0xFF3A2D31),
    outline = Color(0xFF8C7A7C),
    ink = Color(0xFFF4EBE3),
    inkMuted = Color(0xFFBFAEAB),
    primary = Color(0xFFEBA3B8),
    onPrimary = Color(0xFF2A1A1F),
    set1 = Color(0xFFE2B16C),
    onSet1 = Color(0xFF2A1A1F),
    set2 = Color(0xFFD9805F),
    onSet2 = Color(0xFF2A1A1F),
    set3 = Color(0xFF8E3459),
    onSet3 = Color(0xFFFFFFFF),
)
