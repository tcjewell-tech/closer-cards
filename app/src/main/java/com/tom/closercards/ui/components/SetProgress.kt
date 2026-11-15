package com.tom.closercards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

/**
 * Where the players are in the current set: a caption plus one pill per card.
 * Done and current read by shape as well as colour. A skipped card counts as done.
 */
@Composable
fun SetProgress(set: Int, card: Int, modifier: Modifier = Modifier, total: Int = 12) {
    val colors = CloserTheme.colors
    val current = card.coerceIn(1, total)
    val text = "Set $set of 3 · Card $current of $total"
    Column(
        modifier.semantics(mergeDescendants = true) {
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = current.toFloat(),
                range = 1f..total.toFloat(),
                steps = (total - 2).coerceAtLeast(0),
            )
            stateDescription = text
        },
        verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2),
    ) {
        Text(
            text,
            style = CloserTheme.typography.caption,
            color = colors.inkMuted,
            modifier = Modifier.clearAndSetSemantics {},
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in 1..total) {
                val dot = Modifier
                    .size(width = if (i == current) 20.dp else 8.dp, height = 8.dp)
                    .clip(CircleShape)
                Box(
                    if (i <= current) {
                        dot.background(colors.primary)
                    } else {
                        dot.border(1.5.dp, colors.outline, CircleShape)
                    },
                )
            }
        }
    }
}
