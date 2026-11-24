package com.tom.closercards.ui

import androidx.lifecycle.SavedStateHandle
import com.tom.closercards.model.Card
import com.tom.closercards.model.CardSet
import com.tom.closercards.model.Deck
import com.tom.closercards.model.Phase
import com.tom.closercards.model.PlayerNames
import com.tom.closercards.model.QuestionTimer
import org.junit.Assert.assertEquals
import org.junit.Test

class CloserCardsViewModelTest {
    // Two sets of three cards.
    private val deck = Deck(
        listOf(1, 2).map { n ->
            CardSet(n, "Set $n", "guidance", null, (1..3).map { Card("s$n-$it", n, it, "Question $n.$it") })
        },
    )

    @Test
    fun aSessionSurvivesANewViewModelOnTheSameSavedState() {
        val saved = SavedStateHandle()
        CloserCardsViewModel(deck, saved).apply {
            start()
            begin(PlayerNames.resolve("Sam", "Alex"), QuestionTimer.Long)
            startSet()
            next()
            continueToNextSet() // ignored: not at a set boundary
            next()
            next()
            continueToNextSet()
            startSet()
            next()
            flip(true)
        }

        // As after process death: a fresh ViewModel built from the same saved state.
        val restored = CloserCardsViewModel(deck, saved).state.value
        assertEquals(Phase.Card, restored.phase)
        assertEquals(2, restored.setNumber)
        assertEquals(2, restored.cardNumber)
        assertEquals("Alex", restored.firstName)
        assertEquals(true, restored.flipped)
        assertEquals(QuestionTimer.Long, restored.timer)
    }

    @Test
    fun goingHomeIsSavedToo() {
        val saved = SavedStateHandle()
        CloserCardsViewModel(deck, saved).apply {
            start()
            begin(PlayerNames.resolve("", ""), QuestionTimer.Off)
            goHome()
        }
        assertEquals(Phase.Home, CloserCardsViewModel(deck, saved).state.value.phase)
    }

    @Test
    fun indicesFromALargerDeckAreClampedIntoThisOne() {
        val saved = SavedStateHandle(mapOf("phase" to "Card", "setIndex" to 7, "cardIndex" to 40))
        val restored = CloserCardsViewModel(deck, saved).state.value
        assertEquals(Phase.Card, restored.phase)
        assertEquals(2, restored.setNumber)
        assertEquals(3, restored.cardNumber)
        assertEquals("s2-3", restored.cardId)
    }

    @Test
    fun unknownOrMissingValuesFallBackToAFreshSession() {
        val saved = SavedStateHandle(mapOf("phase" to "Renamed", "timer" to "Gone", "cardIndex" to -5))
        val restored = CloserCardsViewModel(deck, saved).state.value
        assertEquals(Phase.Home, restored.phase)
        assertEquals(QuestionTimer.Short, restored.timer)
        assertEquals(1, restored.cardNumber)
        assertEquals("Player A", restored.firstName)
    }
}
