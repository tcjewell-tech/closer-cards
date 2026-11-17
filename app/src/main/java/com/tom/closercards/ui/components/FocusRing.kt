package com.tom.closercards.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.focus.FocusEventModifierNode
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
): Modifier = this then FocusRingElement(shape, color, width, offset)

private data class FocusRingElement(
    val shape: Shape,
    val color: Color,
    val width: Dp,
    val offset: Dp,
) : ModifierNodeElement<FocusRingNode>() {
    override fun create() = FocusRingNode(shape, color, width, offset)

    override fun update(node: FocusRingNode) {
        node.shape = shape
        node.color = color
        node.width = width
        node.offset = offset
        node.invalidateDraw()
    }
}

private class FocusRingNode(
    var shape: Shape,
    var color: Color,
    var width: Dp,
    var offset: Dp,
) : Modifier.Node(), DrawModifierNode, FocusEventModifierNode {
    private var focused = false

    override fun onFocusEvent(focusState: FocusState) {
        if (focused != focusState.isFocused) {
            focused = focusState.isFocused
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (!focused) return
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
