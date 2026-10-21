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

/** The first deck is the main game ("Start"); the rest are listed by title. */
@Composable
fun HomeScreen(decks: List<DeckInfo>, onPick: (DeckInfo) -> Unit) = ScreenColumn {
    Text(
        stringResource(R.string.app_name),
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
    Text(stringResource(R.string.home_how), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    Button(onClick = { onPick(decks.first()) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.start)) }
    decks.drop(1).forEach { deck ->
        OutlinedButton(onClick = { onPick(deck) }, modifier = Modifier.fillMaxWidth()) { Text(deck.title) }
    }
}
