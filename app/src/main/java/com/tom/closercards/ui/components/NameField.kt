package com.tom.closercards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.tom.closercards.ui.theme.CloserShapes
import com.tom.closercards.ui.theme.CloserSizes
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

/** Names are capped so they fit the turn banner. */
const val NAME_MAX_LENGTH = 24

/**
 * Optional player name on Setup. The label sits above the field; the placeholder is never the label.
 * Fall back to "Player A" / "Player B" when the field is left empty.
 */
@Composable
fun NameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    imeAction: ImeAction = ImeAction.Next,
) {
    val colors = CloserTheme.colors
    val type = CloserTheme.typography
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Column(
        modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2),
    ) {
        Text(label, style = type.caption, color = colors.inkMuted)
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.take(NAME_MAX_LENGTH)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = type.body.copy(color = colors.ink),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = imeAction,
            ),
            interactionSource = interaction,
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = CloserSizes.buttonHeight)
                        .clip(CloserShapes.small)
                        .background(colors.surfaceRaised)
                        .border(
                            width = if (focused) 2.dp else 1.5.dp,
                            color = if (focused) colors.primary else colors.outline,
                            shape = CloserShapes.small,
                        )
                        .padding(horizontal = CloserSpacing.space4),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, style = type.body, color = colors.inkMuted)
                    }
                    innerTextField()
                }
            },
        )
    }
}
