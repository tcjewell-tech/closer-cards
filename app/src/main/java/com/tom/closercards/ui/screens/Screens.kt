package com.tom.closercards.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.tom.closercards.R
import com.tom.closercards.model.PlayerNames
import com.tom.closercards.model.QuestionTimer
import com.tom.closercards.ui.SessionUiState
import com.tom.closercards.ui.components.CloserButton
import com.tom.closercards.ui.components.CloserButtonVariant
import com.tom.closercards.ui.components.FlipCard
import com.tom.closercards.ui.components.NameField
import com.tom.closercards.ui.components.SetIntro
import com.tom.closercards.ui.components.SetProgress
import com.tom.closercards.ui.components.TimerPicker
import com.tom.closercards.ui.components.TurnBanner
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

@Composable
fun HomeScreen(onStart: () -> Unit) = ScreenColumn {
    Heading(stringResource(R.string.app_name), CloserTheme.typography.display)
    Body(stringResource(R.string.home_tagline))
    CloserButton(stringResource(R.string.start), onClick = onStart, modifier = Modifier.fillMaxWidth())
}

@Composable
fun SetupScreen(onBegin: (PlayerNames, QuestionTimer) -> Unit) = ScreenColumn {
    var first by rememberSaveable { mutableStateOf("") }
    var second by rememberSaveable { mutableStateOf("") }
    var timer by rememberSaveable { mutableStateOf(QuestionTimer.Short) }
    val defaultA = stringResource(R.string.player_a_default)
    val defaultB = stringResource(R.string.player_b_default)
    Heading(stringResource(R.string.setup_title), CloserTheme.typography.setTitle)
    Body(stringResource(R.string.setup_hint))
    NameField(stringResource(R.string.name_first_label), first, { first = it }, placeholder = defaultA)
    NameField(
        stringResource(R.string.name_second_label),
        second,
        { second = it },
        placeholder = defaultB,
        imeAction = ImeAction.Done,
    )
    TimerPicker(timer, onSelect = { timer = it })
    CloserButton(
        stringResource(R.string.start_set, 1),
        onClick = { onBegin(PlayerNames.resolve(first, second, defaultA, defaultB), timer) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun SetIntroScreen(state: SessionUiState, onStart: () -> Unit) = ScreenColumn {
    SetIntro(
        set = state.setNumber,
        totalSets = state.totalSets,
        title = state.setTitle,
        guidance = state.setGuidance,
        passNote = state.passNote,
    ) { colors ->
        CloserButton(
            stringResource(R.string.start_set, state.setNumber),
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            onSet = colors,
        )
    }
}

@Composable
fun CardScreen(
    state: SessionUiState,
    onFlip: (Boolean) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    // How long this question has been face up. Kept across rotation, back to zero on each new card.
    var elapsedMillis by rememberSaveable(state.cardId) { mutableLongStateOf(0L) }
    val timer = state.timer
    if (timer != QuestionTimer.Off) {
        // Runs only while the question shows, so hiding it pauses the countdown. Counts real frame time
        // rather than an animation, because animations finish at once when system animations are off.
        LaunchedEffect(state.cardId, state.flipped) {
            if (!state.flipped) return@LaunchedEffect
            var last = withFrameMillis { it }
            while (timer.fill(elapsedMillis) < 1f) {
                withFrameMillis {
                    elapsedMillis += it - last
                    last = it
                }
            }
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(CloserSpacing.space4),
        verticalArrangement = Arrangement.spacedBy(CloserSpacing.space6),
    ) {
        SetProgress(state.setNumber, state.totalSets, state.cardNumber, state.totalCards)
        TurnBanner(state.setNumber, state.firstName)
        // The card takes the space left between the banner and the buttons, so Next and Skip never scroll away.
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            key(state.cardId) {
                FlipCard(
                    set = state.setNumber,
                    card = state.cardNumber,
                    totalCards = state.totalCards,
                    question = state.question,
                    flipped = state.flipped,
                    onFlip = onFlip,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
            CloserButton(
                stringResource(R.string.next),
                onClick = onNext,
                modifier = Modifier.weight(1f),
                fill = if (timer == QuestionTimer.Off) null else ({ timer.fill(elapsedMillis) }),
            )
            CloserButton(
                stringResource(R.string.skip),
                onClick = onSkip,
                modifier = Modifier.weight(1f),
                variant = CloserButtonVariant.Secondary,
            )
        }
    }
}

@Composable
fun SetCompleteScreen(state: SessionUiState, onContinue: () -> Unit, onEnd: () -> Unit) = ScreenColumn {
    SetIntro(
        set = state.setNumber,
        totalSets = state.totalSets,
        title = state.setTitle,
        guidance = stringResource(R.string.set_complete_guidance),
        complete = true,
    ) { colors ->
        CloserButton(
            stringResource(R.string.continue_session),
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            onSet = colors,
        )
        CloserButton(
            stringResource(R.string.end_session),
            onClick = onEnd,
            modifier = Modifier.fillMaxWidth(),
            variant = CloserButtonVariant.Secondary,
            onSet = colors,
        )
    }
}

@Composable
fun SessionCompleteScreen(onPlayAgain: () -> Unit) = ScreenColumn {
    Heading(stringResource(R.string.session_complete_title), CloserTheme.typography.display)
    Body(stringResource(R.string.session_complete_body))
    CloserButton(stringResource(R.string.play_again), onClick = onPlayAgain, modifier = Modifier.fillMaxWidth())
}

/** A screen's content, centred when it fits and scrolling when large text makes it taller than the screen. */
@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .fillMaxWidth()
                .padding(horizontal = CloserSpacing.space4, vertical = CloserSpacing.space8),
            verticalArrangement = Arrangement.spacedBy(CloserSpacing.space6, Alignment.CenterVertically),
            content = content,
        )
    }
}

@Composable
private fun Heading(text: String, style: TextStyle) =
    Text(text, style = style, color = CloserTheme.colors.ink, modifier = Modifier.semantics { heading() })

@Composable
private fun Body(text: String) = Text(text, style = CloserTheme.typography.body, color = CloserTheme.colors.inkMuted)
