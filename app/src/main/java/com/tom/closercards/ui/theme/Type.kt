package com.tom.closercards.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/** Text styles from the design system. Sizes are sp so they scale with the system font. */
@Immutable
data class CloserTypography(
    /** App name on Home and the closing line on Session complete. */
    val display: TextStyle,
    /** The question on a revealed card. */
    val question: TextStyle,
    /** Set theme on Set intro and Set complete. */
    val setTitle: TextStyle,
    /** The turn banner. */
    val banner: TextStyle,
    /** Guidance lines, how-it-works, field text. */
    val body: TextStyle,
    /** Button labels. */
    val label: TextStyle,
    /** Progress and the card's position line. */
    val caption: TextStyle,
)

internal fun closerTypography(serif: FontFamily, sans: FontFamily) = CloserTypography(
    display = TextStyle(
        fontFamily = serif,
        fontWeight = FontWeight.Medium,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.01).em,
    ),
    question = TextStyle(
        fontFamily = serif,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 34.sp,
    ),
    setTitle = TextStyle(
        fontFamily = serif,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 38.sp,
    ),
    banner = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    body = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    label = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01.em,
    ),
    caption = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.04.em,
    ),
)

/** Maps the system's styles onto Material 3 slots so stock M3 widgets match. */
internal fun CloserTypography.toMaterial(): Typography = Typography().copy(
    displaySmall = display,
    headlineMedium = setTitle,
    headlineSmall = question,
    titleMedium = banner,
    bodyLarge = body,
    bodyMedium = body,
    bodySmall = caption,
    labelLarge = label,
    labelMedium = caption,
)
