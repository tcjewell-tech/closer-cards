package com.tom.closercards.model

/** The screens of a session, in the order players meet them. */
enum class Phase { Home, Setup, SetIntro, Card, SetComplete, SessionComplete }

enum class Player { A, B }

/** The two names shown in the turn banner. */
data class PlayerNames(val a: String, val b: String) {
    fun of(player: Player): String = if (player == Player.A) a else b

    companion object {
        const val DEFAULT_A = "Player A"
        const val DEFAULT_B = "Player B"

        /** Trims what was typed on Setup; a blank entry falls back to its default. */
        fun resolve(
            typedA: String,
            typedB: String,
            defaultA: String = DEFAULT_A,
            defaultB: String = DEFAULT_B,
        ) = PlayerNames(typedA.trim().ifEmpty { defaultA }, typedB.trim().ifEmpty { defaultB })
    }
}

/**
 * Who answers first. Counting restarts in every set: Player A on odd positions (1, 3, 5...),
 * Player B on even ones, so card 1 of each set always goes to Player A.
 */
object TurnOrder {
    fun firstPlayer(position: Int): Player {
        require(position >= 1) { "Card positions start at 1, got $position" }
        return if (position % 2 == 1) Player.A else Player.B
    }
}

/** The optional countdown for each question, chosen on Setup. It only paces: Next works at any time. */
enum class QuestionTimer(val seconds: Int) {
    Off(0), Short(90), Medium(180), Long(300);

    /** How much of Next is shaded in once a question has been face up for [elapsedMillis]: 0 to 1. */
    fun fill(elapsedMillis: Long): Float =
        if (seconds == 0) 0f else (elapsedMillis / (seconds * 1000f)).coerceIn(0f, 1f)
}

/** Where a session stands. Indices are 0-based into [Deck.sets] and that set's cards. */
data class Session(
    val phase: Phase = Phase.Home,
    val names: PlayerNames = PlayerNames.resolve("", ""),
    val timer: QuestionTimer = QuestionTimer.Short,
    val setIndex: Int = 0,
    val cardIndex: Int = 0,
    val flipped: Boolean = false,
)

/**
 * Every session transition, as pure functions of the current [Session].
 * A call that does not apply to the current phase returns the session unchanged,
 * so a double tap on Next after the last card cannot skip a screen.
 */
class SessionReducer(private val deck: Deck) {

    fun openSetup(s: Session) = if (s.phase == Phase.Home) s.copy(phase = Phase.Setup) else s

    fun begin(s: Session, names: PlayerNames, timer: QuestionTimer = QuestionTimer.Short) =
        if (s.phase == Phase.Setup) Session(phase = Phase.SetIntro, names = names, timer = timer) else s

    fun startSet(s: Session) =
        if (s.phase == Phase.SetIntro) s.copy(phase = Phase.Card, cardIndex = 0, flipped = false) else s

    fun flip(s: Session, flipped: Boolean) = if (s.phase == Phase.Card) s.copy(flipped = flipped) else s

    /** Next: the following card, a set-complete pause after a set's last card, or the end after the last set. */
    fun advance(s: Session): Session {
        if (s.phase != Phase.Card) return s
        val set = deck.sets[s.setIndex]
        return when {
            s.cardIndex + 1 < set.cards.size -> s.copy(cardIndex = s.cardIndex + 1, flipped = false)
            s.setIndex + 1 < deck.sets.size -> s.copy(phase = Phase.SetComplete, flipped = false)
            else -> s.copy(phase = Phase.SessionComplete, flipped = false)
        }
    }

    /** A skipped card counts as done, so Skip moves exactly as Next does. */
    fun skip(s: Session) = advance(s)

    fun continueToNextSet(s: Session) =
        if (s.phase == Phase.SetComplete) {
            s.copy(phase = Phase.SetIntro, setIndex = s.setIndex + 1, cardIndex = 0, flipped = false)
        } else {
            s
        }

    fun endSession(s: Session) = if (s.phase == Phase.SetComplete) s.copy(phase = Phase.SessionComplete) else s

    /** Same players and timer, fresh deck: straight to the first set's intro. */
    fun playAgain(s: Session) =
        if (s.phase == Phase.SessionComplete) Session(phase = Phase.SetIntro, names = s.names, timer = s.timer) else s

    /** Back from any screen: the session ends and Home starts a new one. */
    fun home() = Session()

    /** Pulls a restored session's indices back inside this deck, in case cards.json changed since it was saved. */
    fun fitToDeck(s: Session): Session {
        val setIndex = s.setIndex.coerceIn(0, deck.sets.lastIndex)
        return s.copy(setIndex = setIndex, cardIndex = s.cardIndex.coerceIn(0, deck.sets[setIndex].cards.lastIndex))
    }

    fun currentSet(s: Session): CardSet = deck.sets[s.setIndex]

    fun currentCard(s: Session): Card = currentSet(s).cards[s.cardIndex]

    /** The name in the turn banner for the current card. */
    fun firstName(s: Session): String = s.names.of(TurnOrder.firstPlayer(s.cardIndex + 1))
}
