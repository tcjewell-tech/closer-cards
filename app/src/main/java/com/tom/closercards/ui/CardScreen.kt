package com.tom.closercards.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tom.closercards.R
import com.tom.closercards.model.CARDS_PER_SET
import com.tom.closercards.model.SessionState

@Composable
fun CardScreen(state: SessionState, question: String, onFlip: () -> Unit, onAdvance: () -> Unit) = ScreenColumn {
    Text(
        stringResource(R.string.progress, state.set, state.order, CARDS_PER_SET),
        style = MaterialTheme.typography.titleMedium,
    )
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
        Text(
            stringResource(R.string.turn_banner, state.firstAnswererName),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
    FlipCard(question, state.flipped, onFlip)
    // Skip and Next do the same thing; Skip exists so passing feels as normal as answering.
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onAdvance, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.skip)) }
        Button(onClick = onAdvance, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.next)) }
    }
}

@Composable
private fun FlipCard(question: String, flipped: Boolean, onFlip: () -> Unit) {
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, tween(400), label = "flip")
    val faceUp = rotation > 90f
    val faceDownDescription = stringResource(R.string.card_face_down_desc)

    ElevatedCard(
        onClick = onFlip,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (faceUp) MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card")
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
    ) {
        Box(
            Modifier.fillMaxWidth().heightIn(min = 280.dp).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (faceUp) {
                // Counter-rotate so the question doesn't read mirrored.
                Text(
                    question,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                )
            } else {
                Text(
                    stringResource(R.string.card_face_down),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.semantics { contentDescription = faceDownDescription },
                )
            }
        }
    }
}
