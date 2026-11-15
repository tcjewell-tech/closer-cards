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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSizes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

/**
 * Says who answers first on the current card, in the set colour.
 * Pass the Setup name, or "Player A" / "Player B" when it was left blank:
 * Player A goes first on odd cards, Player B on even cards.
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
            .padding(horizontal = CloserSpacing.space4)
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
        Text("$name answers first", style = CloserTheme.typography.banner, color = setColors.content)
    }
}
