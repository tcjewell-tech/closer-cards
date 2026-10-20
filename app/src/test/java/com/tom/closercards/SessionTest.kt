package com.tom.closercards

import com.tom.closercards.model.Phase
import com.tom.closercards.model.Player
import com.tom.closercards.model.SessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SessionTest {
    private fun started() = SessionState(nameA = "Ann", nameB = "Ben").beginSet()

    private fun SessionState.advanceTimes(n: Int) = (1..n).fold(this) { s, _ -> s.advance() }

    @Test
    fun firstAnswererAlternatesAThenB() {
        var s = started()
        for (order in 1..12) {
            assertEquals(order, s.order)
            assertEquals(if (order % 2 == 1) Player.A else Player.B, s.firstAnswerer)
            assertEquals(if (order % 2 == 1) "Ann" else "Ben", s.firstAnswererName)
            s = s.advance()
        }
    }

    @Test
    fun advanceMovesToNextCardFaceDown() {
        val s = started().flip().advance()
        assertEquals(2, s.order)
        assertFalse(s.flipped)
        assertEquals(Phase.Card, s.phase)
    }

    @Test
    fun skippingWithoutFlippingKeepsProgress() {
        // Skip is advance() on an unflipped card.
        val s = started().advanceTimes(5)
        assertEquals(6, s.order)
        assertEquals(1, s.set)
        assertEquals(Phase.Card, s.phase)
    }

    @Test
    fun lastCardOfSetGoesToSetCompleteThenNextSetIntro() {
        val done = started().advanceTimes(12)
        assertEquals(Phase.SetComplete, done.phase)
        assertEquals(1, done.set)

        val next = done.continueToNextSet()
        assertEquals(Phase.SetIntro, next.phase)
        assertEquals(2, next.set)
        assertEquals(1, next.beginSet().order)
    }

    @Test
    fun lastCardOfSet3EndsSession() {
        val s = started().advanceTimes(12)
            .continueToNextSet().beginSet().advanceTimes(12)
            .continueToNextSet().beginSet().advanceTimes(12)
        assertEquals(3, s.set)
        assertEquals(Phase.Done, s.phase)
    }

    @Test
    fun endSessionFromSetComplete() {
        assertEquals(Phase.Done, started().advanceTimes(12).end().phase)
    }
}
