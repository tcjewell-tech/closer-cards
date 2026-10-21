package com.tom.closercards.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val SET_COUNT = 3
const val CARDS_PER_SET = 12

@Serializable
data class Card(val id: String, val set: Int, val order: Int, val text: String)

/** One entry in assets/decks.json. A non-null [consent] means the deck shows a consent screen first. */
@Serializable
data class DeckInfo(
    val id: String,
    val title: String,
    val file: String,
    val themes: List<String>,
    val guidance: List<String>,
    val consent: String? = null,
)

/** Parses decks.json. Fails fast if ids repeat or a deck lacks a theme and guidance line per set. */
fun loadDeckInfos(json: String): List<DeckInfo> {
    val decks = Json.decodeFromString<List<DeckInfo>>(json)
    require(decks.isNotEmpty() && decks.map { it.id }.toSet().size == decks.size) { "decks.json needs unique deck ids" }
    decks.forEach {
        require(it.themes.size == SET_COUNT && it.guidance.size == SET_COUNT) {
            "Deck ${it.id} needs $SET_COUNT themes and $SET_COUNT guidance lines"
        }
    }
    return decks
}

/** Parses the deck and sorts it by set, then order. Fails fast if the deck isn't exactly 3 sets of 12. */
fun loadDeck(json: String): List<Card> {
    val deck = Json.decodeFromString<List<Card>>(json).sortedWith(compareBy({ it.set }, { it.order }))
    val expected = (1..SET_COUNT).flatMap { s -> (1..CARDS_PER_SET).map { s to it } }
    require(deck.map { it.set to it.order } == expected) {
        "cards.json must hold sets 1-$SET_COUNT with orders 1-$CARDS_PER_SET, each exactly once"
    }
    return deck
}
