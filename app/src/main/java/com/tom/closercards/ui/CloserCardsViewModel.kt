package com.tom.closercards.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tom.closercards.model.Deck
import com.tom.closercards.model.Phase
import com.tom.closercards.model.PlayerNames
import com.tom.closercards.model.QuestionTimer
import com.tom.closercards.model.Session
import com.tom.closercards.model.SessionReducer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Everything the screens show, resolved from the deck and the session. */
data class SessionUiState(
    val phase: Phase,
    val setNumber: Int,
    val totalSets: Int,
    val setTitle: String,
    val setGuidance: String,
    val passNote: String?,
    val cardId: String,
    val cardNumber: Int,
    val totalCards: Int,
    val question: String,
    val firstName: String,
    val flipped: Boolean,
    val timer: QuestionTimer,
)

/**
 * Holds the session and applies [SessionReducer] for each action.
 * The session is mirrored into [savedStateHandle], which Android keeps in memory for this task only:
 * it survives the process being killed in the background, never reaches disk or backup, and is
 * dropped when the task is closed.
 */
class CloserCardsViewModel(
    private val deck: Deck,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val reducer = SessionReducer(deck)
    private var session = reducer.fitToDeck(savedStateHandle.toSession())
    private val _state = MutableStateFlow(uiState())
    val state: StateFlow<SessionUiState> = _state.asStateFlow()

    fun start() = update(reducer::openSetup)
    fun begin(names: PlayerNames, timer: QuestionTimer) = update { reducer.begin(it, names, timer) }
    fun startSet() = update(reducer::startSet)
    fun flip(flipped: Boolean) = update { reducer.flip(it, flipped) }
    fun next() = update(reducer::advance)
    fun skip() = update(reducer::skip)
    fun continueToNextSet() = update(reducer::continueToNextSet)
    fun endSession() = update(reducer::endSession)
    fun playAgain() = update(reducer::playAgain)
    fun goHome() = update { reducer.home() }

    // Called on the main thread only, from click handlers.
    private fun update(change: (Session) -> Session) {
        session = change(session)
        savedStateHandle.save(session)
        _state.value = uiState()
    }

    private fun uiState(): SessionUiState {
        val set = reducer.currentSet(session)
        val card = reducer.currentCard(session)
        return SessionUiState(
            phase = session.phase,
            setNumber = set.number,
            totalSets = deck.sets.size,
            setTitle = set.title,
            setGuidance = set.guidance,
            passNote = set.passNote,
            cardId = card.id,
            cardNumber = session.cardIndex + 1,
            totalCards = set.cards.size,
            question = card.text,
            firstName = reducer.firstName(session),
            flipped = session.flipped,
            timer = session.timer,
        )
    }
}

private const val PHASE = "phase"
private const val NAME_A = "nameA"
private const val NAME_B = "nameB"
private const val TIMER = "timer"
private const val SET = "setIndex"
private const val CARD = "cardIndex"
private const val FLIPPED = "flipped"

private fun SavedStateHandle.save(s: Session) {
    set(PHASE, s.phase.name)
    set(NAME_A, s.names.a)
    set(NAME_B, s.names.b)
    set(TIMER, s.timer.name)
    set(SET, s.setIndex)
    set(CARD, s.cardIndex)
    set(FLIPPED, s.flipped)
}

/** The saved session, or a fresh one. A value this version doesn't recognise falls back to its default. */
private fun SavedStateHandle.toSession(): Session {
    val fresh = Session()
    return Session(
        phase = get<String>(PHASE)?.let { name -> Phase.entries.firstOrNull { it.name == name } } ?: fresh.phase,
        names = PlayerNames(get<String>(NAME_A) ?: fresh.names.a, get<String>(NAME_B) ?: fresh.names.b),
        timer = get<String>(TIMER)?.let { name -> QuestionTimer.entries.firstOrNull { it.name == name } } ?: fresh.timer,
        setIndex = get<Int>(SET) ?: fresh.setIndex,
        cardIndex = get<Int>(CARD) ?: fresh.cardIndex,
        flipped = get<Boolean>(FLIPPED) ?: fresh.flipped,
    )
}
