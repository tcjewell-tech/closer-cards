package com.tom.closercards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tom.closercards.model.CARDS_PER_SET
import com.tom.closercards.model.Card
import com.tom.closercards.model.SessionState
import com.tom.closercards.model.loadDeck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SessionViewModel(app: Application) : AndroidViewModel(app) {
    private val deck: List<Card> =
        loadDeck(app.assets.open("cards.json").bufferedReader().use { it.readText() })

    private val _state = MutableStateFlow(SessionState(nameA = "", nameB = ""))
    val state: StateFlow<SessionState> = _state.asStateFlow()

    fun start(nameA: String, nameB: String) {
        _state.value = SessionState(nameA = nameA, nameB = nameB)
    }

    fun update(transition: SessionState.() -> SessionState) = _state.update(transition)

    fun cardFor(s: SessionState): Card = deck[(s.set - 1) * CARDS_PER_SET + s.order - 1]
}
