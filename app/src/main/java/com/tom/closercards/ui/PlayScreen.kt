package com.tom.closercards.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tom.closercards.R
import com.tom.closercards.SessionViewModel
import com.tom.closercards.model.Phase
import com.tom.closercards.model.SET_COUNT

/** One route for the whole game; set intro, cards, set complete and the ending are phases of the session. */
@Composable
fun PlayScreen(vm: SessionViewModel, onPlayAgain: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    when (state.phase) {
        Phase.SetIntro -> SetIntro(state.set, onBegin = { vm.update { beginSet() } })
        // Keyed per card so a new card starts face down instead of animating back and flashing its question.
        Phase.Card -> key(state.set, state.order) {
            CardScreen(
                state = state,
                question = vm.cardFor(state).text,
                onFlip = { vm.update { flip() } },
                onAdvance = { vm.update { advance() } },
            )
        }
        Phase.SetComplete -> SetComplete(
            set = state.set,
            onContinue = { vm.update { continueToNextSet() } },
            onEnd = { vm.update { end() } },
        )
        Phase.Done -> SessionComplete(onPlayAgain)
    }
}

@Composable
private fun SetIntro(set: Int, onBegin: () -> Unit) = ScreenColumn {
    Text(
        stringResource(R.string.set_label, set, SET_COUNT),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        stringArrayResource(R.array.set_themes)[set - 1],
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
    )
    Text(
        stringArrayResource(R.array.set_guidance)[set - 1],
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
    Button(onClick = onBegin, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.begin_set)) }
}

@Composable
private fun SetComplete(set: Int, onContinue: () -> Unit, onEnd: () -> Unit) = ScreenColumn {
    Text(stringResource(R.string.set_complete_title, set), style = MaterialTheme.typography.headlineMedium)
    Text(
        stringResource(R.string.set_complete_body),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
    Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.continue_set)) }
    OutlinedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.end_session)) }
}

@Composable
private fun SessionComplete(onPlayAgain: () -> Unit) = ScreenColumn {
    Text(stringResource(R.string.done_title), style = MaterialTheme.typography.headlineMedium)
    Text(stringResource(R.string.done_body), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.play_again)) }
}
