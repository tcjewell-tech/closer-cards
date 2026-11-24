package com.tom.closercards.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tom.closercards.model.DeckRepository
import com.tom.closercards.model.Phase
import com.tom.closercards.ui.screens.CardScreen
import com.tom.closercards.ui.screens.HomeScreen
import com.tom.closercards.ui.screens.SessionCompleteScreen
import com.tom.closercards.ui.screens.SetCompleteScreen
import com.tom.closercards.ui.screens.SetIntroScreen
import com.tom.closercards.ui.screens.SetupScreen
import com.tom.closercards.ui.theme.CloserMotion
import com.tom.closercards.ui.theme.rememberReduceMotion

/**
 * Shows the screen for the session's [Phase]; there is no back stack to keep in step.
 * System Back from any screen but Home ends the session and returns Home.
 */
@Composable
fun CloserCardsNavHost() {
    val assets = LocalContext.current.assets
    val viewModel = viewModel { CloserCardsViewModel(DeckRepository.load(assets), createSavedStateHandle()) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fadeMillis = if (rememberReduceMotion()) 0 else CloserMotion.FLIP_MILLIS / 2

    // Keyed by phase: screens fade only when the phase changes, and the outgoing screen keeps drawing its own
    // last state rather than the next phase's. Within a phase (card to card) the screen updates in place.
    // A crossfade; AnimatedContent rather than Crossfade because only it takes a stable contentKey.
    AnimatedContent(
        state,
        transitionSpec = { fadeIn(tween(fadeMillis)) togetherWith fadeOut(tween(fadeMillis)) },
        contentKey = { it.phase },
        label = "screen",
    ) { shown ->
        when (shown.phase) {
            Phase.Home -> HomeScreen(onStart = viewModel::start)
            Phase.Setup -> SetupScreen(onBegin = viewModel::begin)
            Phase.SetIntro -> SetIntroScreen(shown, onStart = viewModel::startSet)
            Phase.Card -> CardScreen(shown, onFlip = viewModel::flip, onNext = viewModel::next, onSkip = viewModel::skip)
            Phase.SetComplete ->
                SetCompleteScreen(shown, onContinue = viewModel::continueToNextSet, onEnd = viewModel::endSession)
            Phase.SessionComplete -> SessionCompleteScreen(onPlayAgain = viewModel::playAgain)
        }
    }

    BackHandler(enabled = state.phase != Phase.Home, onBack = viewModel::goHome)
}
