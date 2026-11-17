package com.tom.closercards

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.tom.closercards.model.DeckRepository
import com.tom.closercards.model.TurnOrder
import com.tom.closercards.model.Player
import org.junit.Rule
import org.junit.Test

/** Plays the bundled deck from Home to Session complete, then Play again. */
class FullSessionTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fullSessionThenPlayAgain() {
        val deck = DeckRepository.load(rule.activity.assets)

        rule.onNodeWithText("Start").performClick()
        val fields = rule.onAllNodes(hasSetTextAction())
        fields[0].performTextInput("Sam")
        // The second name is left blank and falls back to "Player B".
        rule.onNodeWithText("Start set 1").performClick()

        deck.sets.forEachIndexed { index, set ->
            rule.onNodeWithText("Set ${set.number} of ${deck.sets.size}").assertIsDisplayed()
            rule.onNodeWithText(set.title).assertIsDisplayed()
            set.passNote?.let { rule.onNodeWithText(it).assertIsDisplayed() }
            rule.onNodeWithText("Start set ${set.number}").performClick()

            set.cards.forEachIndexed { i, card ->
                val position = i + 1
                val first = if (TurnOrder.firstPlayer(position) == Player.A) "Sam" else "Player B"
                rule.onNodeWithText("$first answers first").assertIsDisplayed()
                rule.onNodeWithContentDescription("Tap to reveal the question", substring = true).performClick()
                rule.onNodeWithContentDescription(card.text, substring = true).assertIsDisplayed()
                // Alternate the two ways forward; both must land on the next card or screen.
                rule.onNodeWithText(if (position % 2 == 1) "Next" else "Skip").performClick()
            }

            if (index < deck.sets.lastIndex) {
                rule.onNodeWithText("Set ${set.number} complete").assertIsDisplayed()
                rule.onNodeWithText("Continue").performClick()
            }
        }

        rule.onNodeWithText("Thank you for tonight").assertIsDisplayed()
        rule.onNodeWithText("Play again").performClick()
        rule.onNodeWithText("Set 1 of ${deck.sets.size}").assertIsDisplayed()
        rule.onAllNodesWithText("Start set 1")[0].assertIsDisplayed()
    }
}
