package com.tom.closercards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

/** Set themes from the spec. */
fun defaultSetTitle(set: Int): String = when (set) {
    2 -> "Values and experiences"
    3 -> "Vulnerability and connection"
    else -> "Light and personal history"
}

/** One line of guidance per set. Sets 2 and 3 are draft copy pending owner review. */
fun defaultSetGuidance(set: Int): String = when (set) {
    2 -> "Take your time. Listen as much as you answer."
    3 -> "Go as deep as feels right tonight."
    else -> "Keep it light; there are no wrong answers."
}

/**
 * The block of set colour that opens a set, or with [complete] closes it as the pause screen.
 * The Set 3 intro always carries the pass note. Buttons in [actions] restyle to stay legible on the set colour.
 */
@Composable
fun SetIntro(
    set: Int,
    modifier: Modifier = Modifier,
    title: String = defaultSetTitle(set),
    guidance: String = defaultSetGuidance(set),
    complete: Boolean = false,
    actions: @Composable ColumnScope.() -> Unit = {},
) {
    val setColors = CloserTheme.colors.forSet(set)
    val type = CloserTheme.typography
    CompositionLocalProvider(
        LocalContentColor provides setColors.content,
        LocalSetBlockColors provides setColors,
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .clip(CloserShapes.card)
                .background(setColors.container)
                .padding(
                    start = CloserSpacing.space6,
                    end = CloserSpacing.space6,
                    top = CloserSpacing.space8,
                    bottom = CloserSpacing.space6,
                ),
            verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2),
        ) {
            Text(
                if (complete) "Set $set complete" else "Set $set of 3",
                style = type.caption,
                color = setColors.content,
            )
            Text(
                title,
                style = type.setTitle,
                color = setColors.content,
                modifier = Modifier
                    .padding(bottom = CloserSpacing.space2)
                    .semantics { heading() },
            )
            Text(guidance, style = type.body, color = setColors.content)
            if (set == 3 && !complete) {
                HorizontalDivider(thickness = 1.dp, color = setColors.content)
                Text("Either of you may pass on any question.", style = type.body, color = setColors.content)
            }
            Column(
                Modifier.padding(top = CloserSpacing.space4),
                verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2),
                content = actions,
            )
        }
    }
}
