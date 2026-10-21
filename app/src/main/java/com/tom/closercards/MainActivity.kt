package com.tom.closercards

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tom.closercards.ui.CloserCardsTheme
import com.tom.closercards.ui.ConsentScreen
import com.tom.closercards.ui.HomeScreen
import com.tom.closercards.ui.PlayScreen
import com.tom.closercards.ui.SetupScreen
import com.tom.closercards.ui.isAdultDeck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Activity-scoped, so the session survives moving between routes and rotation.
            val vm: SessionViewModel = viewModel()
            val nav = rememberNavController()
            val state by vm.state.collectAsStateWithLifecycle()
            val route = nav.currentBackStackEntryAsState().value?.destination?.route
            // Home always shows the main palette; once a deck is picked, its palette takes over.
            val themeDeck = if (route == null || route == "home") "main" else state.deckId

            // Adult decks force a dark palette, so force light status bar icons to match.
            LaunchedEffect(themeDeck) {
                enableEdgeToEdge(
                    if (isAdultDeck(themeDeck)) SystemBarStyle.dark(Color.TRANSPARENT)
                    else SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
                )
            }

            CloserCardsTheme(deckId = themeDeck) {
                Surface(Modifier.fillMaxSize()) {
                    NavHost(nav, startDestination = "home", modifier = Modifier.safeDrawingPadding()) {
                        composable("home") {
                            HomeScreen(vm.decks, onPick = { deck ->
                                vm.selectDeck(deck.id)
                                nav.navigate(if (deck.consent != null) "consent" else "setup")
                            })
                        }
                        composable("consent") {
                            ConsentScreen(
                                deck = vm.deck(state.deckId),
                                onAgree = { nav.navigate("setup") },
                                onBack = { nav.popBackStack() },
                            )
                        }
                        composable("setup") {
                            SetupScreen(onStart = { a, b ->
                                vm.start(a, b)
                                nav.navigate("play")
                            })
                        }
                        composable("play") {
                            PlayScreen(vm, onPlayAgain = { nav.popBackStack("setup", inclusive = false) })
                        }
                    }
                }
            }
        }
    }
}
