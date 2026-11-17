package com.tom.closercards.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Spacing tokens. */
object CloserSpacing {
    /** Caption to what it labels; between progress dots. */
    val space2 = 8.dp
    /** Screen side gutter; between stacked buttons. */
    val space4 = 16.dp
    /** Card padding; banner to card. */
    val space6 = 24.dp
    /** Between screen sections. */
    val space8 = 32.dp
}

/** Radius tokens as shapes. */
object CloserShapes {
    /** Text fields and the turn banner. */
    val small = RoundedCornerShape(8.dp)
    /** The card and the set-intro block. */
    val card = RoundedCornerShape(24.dp)
    /** Buttons and progress dots. */
    val pill = RoundedCornerShape(percent = 50)
}

/** Size tokens. */
object CloserSizes {
    /** Minimum size of every tappable control. */
    val touchTarget = 48.dp
    /** Start, Next, Skip, Continue, Play again. */
    val buttonHeight = 52.dp
    /** Card width cap; the card is 3:4. */
    val cardMaxWidth = 360.dp
    /** Closest Compose elevation to the shadow-card token. Only the card casts a shadow. */
    val cardElevation = 8.dp
}

/**
 * The flip: about 400ms, ease in-out, faces swap at 90 degrees. With system animations off the
 * faces swap instantly (Compose scales every animation by the same setting, so a fade would be 0ms anyway).
 */
object CloserMotion {
    const val FLIP_MILLIS = 400
    val FlipEasing = CubicBezierEasing(0.45f, 0f, 0.55f, 1f)
}
