package com.tom.closercards.ui

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

/** Holds the session in memory (nothing is saved) and applies [SessionReducer] for each action. */
class CloserCardsViewModel(private val deck: Deck) : ViewModel() {
    private val reducer = SessionReducer(deck)
    private var session = Session()
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
