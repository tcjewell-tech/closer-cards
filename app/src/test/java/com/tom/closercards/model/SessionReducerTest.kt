package com.tom.closercards.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionReducerTest {
    // Two sets of three: odd counts show that turn order restarts per set.
    private val deck = Deck(
        listOf(1, 2).map { n ->
            CardSet(n, "Set $n", "guidance", null, (1..3).map { Card("s$n-$it", n, it, "Question $n.$it") })
        },
    )
    private val reducer = SessionReducer(deck)

    private fun inCard(names: PlayerNames = PlayerNames.resolve("", "")): Session {
        val setup = reducer.openSetup(Session())
        return reducer.startSet(reducer.begin(setup, names))
    }

    @Test
    fun oddPositionsGoToPlayerAEvenToPlayerB() {
        assertEquals(listOf(Player.A, Player.B, Player.A, Player.B), (1..4).map(TurnOrder::firstPlayer))
    }

    @Test
    fun turnOrderRestartsEverySetWithCustomNames() {
        var s = inCard(PlayerNames.resolve("  Sam ", "Alex"))
        val firsts = mutableListOf<String>()
        while (s.phase != Phase.SessionComplete) {
            if (s.phase == Phase.Card) firsts += reducer.firstName(s)
            s = when (s.phase) {
                Phase.Card -> reducer.advance(s)
                Phase.SetComplete -> reducer.continueToNextSet(s)
                Phase.SetIntro -> reducer.startSet(s)
                else -> error("unexpected ${s.phase}")
            }
        }
        assertEquals(listOf("Sam", "Alex", "Sam", "Sam", "Alex", "Sam"), firsts)
    }

    @Test
    fun blankNamesFallBackToDefaults() {
        val s = inCard(PlayerNames.resolve(" ", ""))
        assertEquals("Player A", reducer.firstName(s))
        assertEquals("Player B", reducer.firstName(reducer.advance(s)))
    }

    @Test
    fun nextAndSkipMoveIdenticallyThroughTheWholeSession() {
        var viaNext = inCard()
        var viaSkip = inCard()
        repeat(6) {
            assertEquals(viaNext, viaSkip)
            viaNext = reducer.advance(viaNext).let { if (it.phase == Phase.SetComplete) reducer.continueToNextSet(it) else it }
                .let { if (it.phase == Phase.SetIntro) reducer.startSet(it) else it }
            viaSkip = reducer.skip(viaSkip).let { if (it.phase == Phase.SetComplete) reducer.continueToNextSet(it) else it }
                .let { if (it.phase == Phase.SetIntro) reducer.startSet(it) else it }
        }
        assertEquals(Phase.SessionComplete, viaNext.phase)
        assertEquals(viaNext, viaSkip)
    }

    @Test
    fun setBoundaryPausesAndLastSetEndsTheSession() {
        var s = inCard()
        s = reducer.advance(reducer.advance(s))
        assertEquals(Phase.Card, s.phase)
        s = reducer.skip(s)
        assertEquals(Phase.SetComplete, s.phase)
        s = reducer.startSet(reducer.continueToNextSet(s))
        assertEquals(1, s.setIndex)
        assertEquals(0, s.cardIndex)
        s = reducer.skip(reducer.advance(reducer.advance(s)))
        assertEquals(Phase.SessionComplete, s.phase)
    }

    @Test
    fun flipResetsOnNextAndSkip() {
        val flipped = reducer.flip(inCard(), true)
        assertTrue(flipped.flipped)
        assertFalse(reducer.advance(flipped).flipped)
        assertFalse(reducer.skip(flipped).flipped)
    }

    @Test
    fun endSessionFromAPauseAndPlayAgainKeepsNames() {
        val names = PlayerNames.resolve("Sam", "Alex")
        val paused = reducer.skip(reducer.skip(reducer.skip(inCard(names))))
        val ended = reducer.endSession(paused)
        assertEquals(Phase.SessionComplete, ended.phase)
        assertEquals(Session(phase = Phase.SetIntro, names = names), reducer.playAgain(ended))
    }

    @Test
    fun timerFillsOverItsLengthAndPlayAgainKeepsTheChoice() {
        assertEquals(0f, QuestionTimer.Short.fill(0), 0f)
        assertEquals(0.5f, QuestionTimer.Short.fill(45_000), 0.001f)
        assertEquals(1f, QuestionTimer.Long.fill(999_000), 0f)
        assertEquals(0f, QuestionTimer.Off.fill(999_000), 0f)

        val begun = reducer.begin(reducer.openSetup(Session()), PlayerNames.resolve("", ""), QuestionTimer.Long)
        val ended = reducer.endSession(reducer.skip(reducer.skip(reducer.skip(reducer.startSet(begun)))))
        assertEquals(QuestionTimer.Long, reducer.playAgain(ended).timer)
    }

    @Test
    fun actionsOutsideTheirPhaseChangeNothing() {
        val done = reducer.endSession(reducer.skip(reducer.skip(reducer.skip(inCard()))))
        assertEquals(done, reducer.advance(done))
        assertEquals(done, reducer.continueToNextSet(done))
        assertEquals(done, reducer.flip(done, true))
        val home = Session()
        assertEquals(home, reducer.startSet(home))
    }
}
