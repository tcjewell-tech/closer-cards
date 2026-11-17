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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tom.closercards.R
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme
import com.tom.closercards.ui.theme.SetColors

/**
 * The block of set colour that opens a set, or with [complete] closes it as the pause screen.
 * Title, guidance and the optional [passNote] come from the deck (cards.json).
 * [actions] receives the set's colours: pass them to each CloserButton as `onSet`.
 */
@Composable
fun SetIntro(
    set: Int,
    totalSets: Int,
    title: String,
    guidance: String,
    modifier: Modifier = Modifier,
    passNote: String? = null,
    complete: Boolean = false,
    actions: @Composable ColumnScope.(SetColors) -> Unit = {},
) {
    val setColors = CloserTheme.colors.forSet(set)
    val type = CloserTheme.typography
    CompositionLocalProvider(LocalContentColor provides setColors.content) {
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
                if (complete) {
                    stringResource(R.string.set_complete, set)
                } else {
                    stringResource(R.string.set_of, set, totalSets)
                },
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
            if (passNote != null) {
                HorizontalDivider(thickness = 1.dp, color = setColors.content)
                Text(passNote, style = type.body, color = setColors.content)
            }
            Column(
                Modifier.padding(top = CloserSpacing.space4),
                verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2),
            ) { actions(setColors) }
        }
    }
}
