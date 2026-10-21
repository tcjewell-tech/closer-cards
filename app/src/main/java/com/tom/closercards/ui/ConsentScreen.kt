package com.tom.closercards.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.tom.closercards.R
import com.tom.closercards.model.DeckInfo

@Composable
fun ConsentScreen(deck: DeckInfo, onAgree: () -> Unit, onBack: () -> Unit) = ScreenColumn {
    Text(deck.title, style = MaterialTheme.typography.headlineMedium)
    Text(deck.consent.orEmpty(), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    Button(onClick = onAgree, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.consent_agree)) }
    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.back)) }
}
