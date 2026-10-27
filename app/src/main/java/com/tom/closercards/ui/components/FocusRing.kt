package com.tom.closercards.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The system's focus ring: solid, 2dp, offset 2dp outside the control.
 * Place it before the clickable or focusable it should follow.
 */
internal fun Modifier.focusRing(
    shape: Shape,
    color: Color,
    width: Dp = 2.dp,
    offset: Dp = 2.dp,
): Modifier = composed {
    var focused by remember { mutableStateOf(false) }
    this
        .onFocusChanged { focused = it.isFocused }
        .drawWithContent {
            drawContent()
            if (focused) {
                val strokePx = width.toPx()
                val inset = offset.toPx() + strokePx / 2
                val outline = shape.createOutline(
                    Size(size.width + inset * 2, size.height + inset * 2),
                    layoutDirection,
                    this,
                )
                translate(-inset, -inset) {
                    drawOutline(outline, color, style = Stroke(strokePx))
                }
            }
        }
}
