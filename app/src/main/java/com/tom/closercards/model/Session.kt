package com.tom.closercards.model

enum class Player { A, B }

enum class Phase { SetIntro, Card, SetComplete, Done }

/** In-memory state of one play session. Every transition returns a new copy. */
data class SessionState(
    val nameA: String,
    val nameB: String,
    val deckId: String = "main",
    val set: Int = 1,
    val order: Int = 1,
    val phase: Phase = Phase.SetIntro,
    val flipped: Boolean = false,
) {
    /** Player A answers first on odd cards, Player B on even cards. */
    val firstAnswerer: Player get() = if (order % 2 == 1) Player.A else Player.B

    val firstAnswererName: String get() = if (firstAnswerer == Player.A) nameA else nameB

    fun beginSet() = copy(phase = Phase.Card, order = 1, flipped = false)

    fun flip() = copy(flipped = !flipped)

    /** Next and Skip both land here: move on, or close the set after its last card. */
    fun advance() = when {
        order < CARDS_PER_SET -> copy(order = order + 1, flipped = false)
        set < SET_COUNT -> copy(phase = Phase.SetComplete, flipped = false)
        else -> copy(phase = Phase.Done, flipped = false)
    }

    fun continueToNextSet() = copy(set = set + 1, order = 1, phase = Phase.SetIntro)

    fun end() = copy(phase = Phase.Done)
}
