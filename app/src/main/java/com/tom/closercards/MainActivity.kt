package com.tom.closercards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tom.closercards.ui.theme.CloserCardsTheme
import com.tom.closercards.ui.theme.CloserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloserCardsTheme {
                Surface(Modifier.fillMaxSize(), color = CloserTheme.colors.surface) {
                    // Placeholder until the navigation graph lands.
                    Box(contentAlignment = Alignment.Center) {
                        Text("Closer Cards", style = CloserTheme.typography.display)
                    }
                }
            }
        }
    }
}
