package com.tom.closercards.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tom.closercards.model.DeckRepository
import com.tom.closercards.model.Phase
import com.tom.closercards.ui.screens.CardScreen
import com.tom.closercards.ui.screens.HomeScreen
import com.tom.closercards.ui.screens.SessionCompleteScreen
import com.tom.closercards.ui.screens.SetCompleteScreen
import com.tom.closercards.ui.screens.SetIntroScreen
import com.tom.closercards.ui.screens.SetupScreen

/**
 * One route per [Phase]. The ViewModel's phase decides the screen and the back stack only ever
 * holds that one screen, so system Back is handled here: it ends the session and returns Home.
 */
@Composable
fun CloserCardsNavHost() {
    val assets = LocalContext.current.assets
    val viewModel = viewModel { CloserCardsViewModel(DeckRepository.load(assets)) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val nav = rememberNavController()

    // Navigate in the same frame as the state change, so no screen ever draws another phase's state.
    fun syncRoute() {
        val route = viewModel.state.value.phase.name
        if (nav.currentDestination?.route != route) {
            nav.navigate(route) { popUpTo(nav.graph.id) { inclusive = true } }
        }
    }

    fun go(action: () -> Unit) {
        action()
        syncRoute()
    }

    NavHost(
        navController = nav,
        startDestination = Phase.Home.name,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(Phase.Home.name) { HomeScreen(onStart = { go(viewModel::start) }) }
        composable(Phase.Setup.name) { SetupScreen(onBegin = { names, timer -> go { viewModel.begin(names, timer) } }) }
        composable(Phase.SetIntro.name) { SetIntroScreen(state, onStart = { go(viewModel::startSet) }) }
        composable(Phase.Card.name) {
            CardScreen(state, onFlip = viewModel::flip, onNext = { go(viewModel::next) }, onSkip = { go(viewModel::skip) })
        }
        composable(Phase.SetComplete.name) {
            SetCompleteScreen(
                state,
                onContinue = { go(viewModel::continueToNextSet) },
                onEnd = { go(viewModel::endSession) },
            )
        }
        composable(Phase.SessionComplete.name) { SessionCompleteScreen(onPlayAgain = { go(viewModel::playAgain) }) }
    }

    // After process death the ViewModel starts at Home while the restored back stack may not.
    LaunchedEffect(Unit) { syncRoute() }
    BackHandler(enabled = state.phase != Phase.Home, onBack = { go(viewModel::goHome) })
}
