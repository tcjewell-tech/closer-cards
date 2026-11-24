package com.tom.closercards.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.tom.closercards.R
import com.tom.closercards.model.QuestionTimer
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

/**
 * Setup's choice of countdown for each question: Off or one of three lengths.
 * Two rows of two, so the labels still fit at the largest system font size.
 */
@Composable
fun TimerPicker(selected: QuestionTimer, onSelect: (QuestionTimer) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2)) {
        Text(stringResource(R.string.timer_label), style = CloserTheme.typography.caption, color = CloserTheme.colors.inkMuted)
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(CloserSpacing.space2)) {
            QuestionTimer.entries.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(CloserSpacing.space2)) {
                    pair.forEach { timer ->
                        val isSelected = timer == selected
                        CloserButton(
                            stringResource(timer.label),
                            onClick = { onSelect(timer) },
                            modifier = Modifier.weight(1f).semantics {
                                this.selected = isSelected
                                role = Role.RadioButton
                            },
                            variant = if (isSelected) CloserButtonVariant.Primary else CloserButtonVariant.Secondary,
                        )
                    }
                }
            }
        }
        Text(stringResource(R.string.timer_hint), style = CloserTheme.typography.caption, color = CloserTheme.colors.inkMuted)
    }
}

private val QuestionTimer.label: Int
    get() = when (this) {
        QuestionTimer.Off -> R.string.timer_off
        QuestionTimer.Short -> R.string.timer_short
        QuestionTimer.Medium -> R.string.timer_medium
        QuestionTimer.Long -> R.string.timer_long
    }
