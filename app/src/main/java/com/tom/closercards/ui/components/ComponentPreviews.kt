package com.tom.closercards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.tom.closercards.ui.theme.CloserCardsTheme
import com.tom.closercards.ui.theme.CloserSpacing
import com.tom.closercards.ui.theme.CloserTheme

// Preview copy only; the app takes set titles and guidance from cards.json.

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    CloserCardsTheme {
        Box(
            Modifier
                .background(CloserTheme.colors.surface)
                .padding(CloserSpacing.space6),
        ) { content() }
    }
}

@PreviewLightDark
@Composable
private fun ButtonPreview() = PreviewSurface {
    Column(verticalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
        Row(horizontalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
            CloserButton("Next", onClick = {})
            CloserButton("Skip", onClick = {}, variant = CloserButtonVariant.Secondary)
            CloserButton("Start", onClick = {}, enabled = false)
        }
        CloserButton("Play again", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@PreviewLightDark
@Composable
private fun FlipCardPreview() = PreviewSurface {
    var flipped by remember { mutableStateOf(false) }
    Box(Modifier.padding(8.dp)) {
        FlipCard(
            set = 2,
            card = 10,
            totalCards = 12,
            question = "What does home mean to you?",
            flipped = flipped,
            onFlip = { flipped = it },
        )
    }
}

@PreviewLightDark
@Composable
private fun TurnBannerPreview() = PreviewSurface {
    Column(verticalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
        TurnBanner(set = 1, name = "Sam")
        TurnBanner(set = 2, name = "Alex")
        TurnBanner(set = 3, name = "Player B")
    }
}

@PreviewLightDark
@Composable
private fun SetProgressPreview() = PreviewSurface {
    Column(verticalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
        SetProgress(set = 1, totalSets = 3, card = 1, totalCards = 12)
        SetProgress(set = 2, totalSets = 3, card = 5, totalCards = 12)
        SetProgress(set = 3, totalSets = 3, card = 12, totalCards = 12)
    }
}

@PreviewLightDark
@Composable
private fun SetIntroPreview() = PreviewSurface {
    Column(verticalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
        SetIntro(
            set = 3,
            totalSets = 3,
            title = "Vulnerability and connection",
            guidance = "Go as deep as feels right tonight.",
            passNote = "Either of you may pass on any question.",
        ) { colors ->
            CloserButton("Start set 3", onClick = {}, modifier = Modifier.fillMaxWidth(), onSet = colors)
        }
        SetIntro(
            set = 1,
            totalSets = 3,
            title = "Light and personal history",
            guidance = "Take a breath. Carry on when you are both ready.",
            complete = true,
        ) { colors ->
            CloserButton("Continue", onClick = {}, modifier = Modifier.fillMaxWidth(), onSet = colors)
            CloserButton(
                "End session",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = CloserButtonVariant.Secondary,
                onSet = colors,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NameFieldPreview() = PreviewSurface {
    Column(verticalArrangement = Arrangement.spacedBy(CloserSpacing.space4)) {
        NameField("First player", value = "Sam", onValueChange = {}, placeholder = "Player A")
        NameField("Second player", value = "", onValueChange = {}, placeholder = "Player B")
    }
}
