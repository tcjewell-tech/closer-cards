package com.tom.closercards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tom.closercards.ui.CloserCardsNavHost
import com.tom.closercards.ui.theme.CloserCardsTheme
import com.tom.closercards.ui.theme.CloserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloserCardsTheme {
                Surface(Modifier.fillMaxSize(), color = CloserTheme.colors.surface) {
                    CloserCardsNavHost()
                }
            }
        }
    }
}
