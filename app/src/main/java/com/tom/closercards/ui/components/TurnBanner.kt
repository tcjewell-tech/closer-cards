package com.tom.closercards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tom.closercards.R
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSizes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

/**
 * Says who answers first on the current card, in the set colour.
 * Pass the name the session resolved (model/TurnOrder.kt decides whose turn it is).
 */
@Composable
fun TurnBanner(set: Int, name: String, modifier: Modifier = Modifier) {
    val setColors = CloserTheme.colors.forSet(set)
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = CloserSizes.touchTarget)
            .clip(CloserShapes.small)
            .background(setColors.container)
            // Vertical padding only shows once the text wraps (long name, large font).
            .padding(horizontal = CloserSpacing.space4, vertical = CloserSpacing.space2)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CloserSpacing.space2),
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(setColors.content),
        )
        Text(
            stringResource(R.string.turn_banner, name),
            style = CloserTheme.typography.banner,
            color = setColors.content,
        )
    }
}
