package com.tom.closercards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tom.closercards.ui.CloserCardsTheme
import com.tom.closercards.ui.HomeScreen
import com.tom.closercards.ui.PlayScreen
import com.tom.closercards.ui.SetupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloserCardsTheme {
                Surface(Modifier.fillMaxSize()) {
                    // Activity-scoped, so the session survives moving between routes and rotation.
                    val vm: SessionViewModel = viewModel()
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = "home", modifier = Modifier.safeDrawingPadding()) {
                        composable("home") { HomeScreen(onStart = { nav.navigate("setup") }) }
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
