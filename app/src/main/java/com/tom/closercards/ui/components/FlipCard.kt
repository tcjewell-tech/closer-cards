package com.tom.closercards.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tom.closercards.R
import com.tom.closercards.ui.theme.CloserMotion
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSizes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme
import com.tom.closercards.ui.theme.rememberReduceMotion

/**
 * The card. Face down it shows the set colour and its position; a tap flips it to the question.
 * State is hoisted: keep [flipped] in the session ViewModel and reset it to false on Next or Skip.
 * Key it by card id so a new card starts face down instead of flipping back over the next question.
 * With system animations off the faces swap without the flip.
 * It fits the space it is given at 3:4, up to [CloserSizes.cardMaxWidth] wide.
 */
@Composable
fun FlipCard(
    set: Int,
    card: Int,
    totalCards: Int,
    question: String,
    flipped: Boolean,
    onFlip: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.card_hint),
) {
    val colors = CloserTheme.colors
    val reduceMotion = rememberReduceMotion()
    val position = stringResource(R.string.card_position, set, card, totalCards)
    val rotation by animateFloatAsState(
        targetValue = if (flipped && !reduceMotion) 180f else 0f,
        animationSpec = tween(CloserMotion.FLIP_MILLIS, easing = CloserMotion.FlipEasing),
        label = "cardFlip",
    )
    val description = if (flipped) {
        stringResource(R.string.card_revealed_description, position, question)
    } else {
        stringResource(R.string.card_hidden_description, position)
    }
    val clickLabel = stringResource(if (flipped) R.string.card_hide_action else R.string.card_reveal_action)

    Box(
        modifier
            .widthIn(max = CloserSizes.cardMaxWidth)
            .aspectRatio(3f / 4f)
            .focusRing(CloserShapes.card, colors.primary)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .shadow(CloserSizes.cardElevation, CloserShapes.card)
            .clickable(role = Role.Button, onClickLabel = clickLabel) { onFlip(!flipped) }
            .semantics { contentDescription = description },
    ) {
        Box(Modifier.fillMaxSize().clearAndSetSemantics {}) {
            when {
                reduceMotion -> if (flipped) CardFront(position, question) else CardBack(set, position, hint)
                rotation > 90f -> CardFront(position, question, Modifier.graphicsLayer { rotationY = 180f })
                else -> CardBack(set, position, hint)
            }
        }
    }
}

@Composable
private fun CardBack(set: Int, position: String, hint: String) {
    val setColors = CloserTheme.colors.forSet(set)
    val type = CloserTheme.typography
    Column(
        Modifier
            .fillMaxSize()
            .background(setColors.container)
            // Set 1 in light and set 3 in dark sit under 3:1 against surface; the outline keeps the edge.
            .border(1.dp, CloserTheme.colors.outline, CloserShapes.card)
            .padding(CloserSpacing.space6),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(position, style = type.caption, color = setColors.content)
        Text(hint, style = type.body, color = setColors.content)
    }
}

@Composable
private fun CardFront(position: String, question: String, modifier: Modifier = Modifier) {
    val colors = CloserTheme.colors
    val type = CloserTheme.typography
    Column(
        modifier
            .fillMaxSize()
            .background(colors.surfaceRaised)
            .border(1.dp, colors.hairline, CloserShapes.card)
            .padding(CloserSpacing.space6)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(CloserSpacing.space4),
    ) {
        Text(position, style = type.caption, color = colors.inkMuted)
        // Never truncate a question: long ones scroll inside the card.
        Text(question, style = type.question, color = colors.ink)
    }
}
