package com.tom.closercards.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSizes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme
import com.tom.closercards.ui.theme.SetColors

/** How far the not-yet-shaded part of a button is mixed towards the page colour. At 0.25 the label stays above 4.5:1 on it. */
private const val FILL_WASH = 0.25f

enum class CloserButtonVariant {
    /** The one main action per screen: Start, Next, Continue, Play again. */
    Primary,

    /** Outlined: Skip and End session. Never styled as a warning. */
    Secondary,
}

/**
 * Pill button. One Primary per screen; Skip sits beside Next as Secondary.
 * Labels are sentence case. An icon never replaces the label.
 * On a block of set colour, pass that set's [onSet] colours (SetIntro hands them to its actions)
 * so the button stays legible and its edge stays visible against the set colour.
 * [fill] shades the pill in from the start edge: the fraction (0 to 1) it returns is at full colour and
 * the rest is washed out. It is read while drawing, so a changing value does not recompose the button.
 */
@Composable
fun CloserButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: CloserButtonVariant = CloserButtonVariant.Primary,
    enabled: Boolean = true,
    onSet: SetColors? = null,
    icon: (@Composable () -> Unit)? = null,
    fill: (() -> Float)? = null,
) {
    val colors = CloserTheme.colors
    val (container, content, border) = when (variant) {
        CloserButtonVariant.Primary ->
            if (onSet != null) {
                // surfaceRaised alone is under 3:1 on set 1 (light) and set 3 (dark); the border keeps the pill's edge.
                Triple(colors.surfaceRaised, colors.ink, BorderStroke(1.dp, onSet.content))
            } else {
                Triple(colors.primary, colors.onPrimary, null)
            }
        CloserButtonVariant.Secondary -> {
            val line = onSet?.content ?: colors.outline
            Triple(Color.Transparent, onSet?.content ?: colors.ink, BorderStroke(1.5.dp, line))
        }
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .focusRing(CloserShapes.pill, onSet?.content ?: colors.primary)
            .alpha(if (enabled) 1f else 0.45f)
            .heightIn(min = CloserSizes.buttonHeight)
            .widthIn(min = CloserSizes.touchTarget)
            .then(
                if (fill == null) {
                    Modifier
                } else {
                    // Drawn under the label (the Button's own container is transparent below), so the label keeps its colour.
                    val washed = lerp(container, colors.surface, FILL_WASH)
                    Modifier.clip(CloserShapes.pill).drawBehind {
                        drawRect(washed)
                        val full = size.width * fill().coerceIn(0f, 1f)
                        val left = if (layoutDirection == LayoutDirection.Rtl) size.width - full else 0f
                        drawRect(container, topLeft = Offset(left, 0f), size = Size(full, size.height))
                    }
                },
            ),
        enabled = enabled,
        shape = CloserShapes.pill,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (fill == null) container else Color.Transparent,
            contentColor = content,
            disabledContainerColor = container,
            disabledContentColor = content,
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = CloserSpacing.space6),
    ) {
        if (icon != null) {
            Box(Modifier.size(20.dp)) { icon() }
            Spacer(Modifier.width(CloserSpacing.space2))
        }
        Text(text, style = CloserTheme.typography.label)
    }
}
