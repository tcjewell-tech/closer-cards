package com.tom.closercards.model

import android.content.res.AssetManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** One question. [set] and [order] place it in the deck. */
@Serializable
data class Card(val id: String, val set: Int, val order: Int, val text: String)

/** A set with its copy and its cards in play order. [number] runs 1 to the deck's set count. */
data class CardSet(
    val number: Int,
    val title: String,
    val guidance: String,
    val passNote: String?,
    val cards: List<Card>,
)

/** The whole deck, sets in play order. */
data class Deck(val sets: List<CardSet>)

/** A set's copy as written in cards.json. */
@Serializable
private data class SetInfo(val set: Int, val title: String, val guidance: String, val passNote: String? = null)

/** The shape of cards.json. */
@Serializable
private data class DeckFile(val sets: List<SetInfo>, val cards: List<Card>)

/**
 * Loads the bundled deck. cards.json is edited by hand, so it is checked here and a broken file
 * fails at launch with a message naming the problem rather than misbehaving mid-session.
 */
object DeckRepository {
    const val ASSET_NAME = "cards.json"

    fun load(assets: AssetManager): Deck =
        assets.open(ASSET_NAME).bufferedReader().use { parse(it.readText()) }

    fun parse(json: String): Deck {
        val file = Json.decodeFromString<DeckFile>(json)
        val sets = file.sets.sortedBy { it.set }
        val numbers = sets.map { it.set }
        require(sets.isNotEmpty()) { "cards.json has no sets" }
        require(numbers == (1..sets.size).toList()) { "Sets must be numbered 1 to ${sets.size}, found $numbers" }

        val duplicateIds = file.cards.groupBy { it.id }.filterValues { it.size > 1 }.keys
        require(duplicateIds.isEmpty()) { "Duplicate card ids: $duplicateIds" }
        file.cards.forEach { card ->
            require(card.set in numbers) { "Card ${card.id} is in set ${card.set}, which is not listed in sets" }
            require(card.text.isNotBlank()) { "Card ${card.id} has no text" }
        }

        return Deck(
            sets.map { info ->
                val cards = file.cards.filter { it.set == info.set }.sortedBy { it.order }
                require(cards.isNotEmpty()) { "Set ${info.set} has no cards" }
                require(cards.map { it.order }.toSet().size == cards.size) { "Set ${info.set} repeats an order number" }
                CardSet(info.set, info.title, info.guidance, info.passNote, cards)
            },
        )
    }
}
