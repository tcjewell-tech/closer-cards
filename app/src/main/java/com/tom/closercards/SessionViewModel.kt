package com.tom.closercards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tom.closercards.model.CARDS_PER_SET
import com.tom.closercards.model.Card
import com.tom.closercards.model.DeckInfo
import com.tom.closercards.model.SessionState
import com.tom.closercards.model.loadDeck
import com.tom.closercards.model.loadDeckInfos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SessionViewModel(app: Application) : AndroidViewModel(app) {
    private val assets = app.assets
    private fun readAsset(name: String) = assets.open(name).bufferedReader().use { it.readText() }

    val decks: List<DeckInfo> = loadDeckInfos(readAsset("decks.json"))
    private val cards = mutableMapOf<String, List<Card>>()

    private val _state = MutableStateFlow(SessionState(nameA = "", nameB = "", deckId = decks.first().id))
    val state: StateFlow<SessionState> = _state.asStateFlow()

    fun deck(id: String): DeckInfo = decks.first { it.id == id }

    fun selectDeck(id: String) {
        _state.value = SessionState(nameA = "", nameB = "", deckId = id)
    }

    fun start(nameA: String, nameB: String) = _state.update { SessionState(nameA = nameA, nameB = nameB, deckId = it.deckId) }

    fun update(transition: SessionState.() -> SessionState) = _state.update(transition)

    fun cardFor(s: SessionState): Card {
        val deckCards = cards.getOrPut(s.deckId) { loadDeck(readAsset(deck(s.deckId).file)) }
        return deckCards[(s.set - 1) * CARDS_PER_SET + s.order - 1]
    }
}
