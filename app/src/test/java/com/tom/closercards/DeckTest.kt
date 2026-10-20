package com.tom.closercards

import com.tom.closercards.model.Card
import com.tom.closercards.model.loadDeck
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeckTest {
    // Unit tests run with the module directory (app/) as the working directory.
    private val json = File("src/main/assets/cards.json").readText()
    private val deck = loadDeck(json)

    @Test
    fun loads36CardsIn3SetsOf12() {
        assertEquals(36, deck.size)
        assertEquals(mapOf(1 to 12, 2 to 12, 3 to 12), deck.groupingBy { it.set }.eachCount())
    }

    @Test
    fun ordersBySetThenOrderRegardlessOfFileOrder() {
        val shuffled = Json.encodeToString(Json.decodeFromString<List<Card>>(json).shuffled(kotlin.random.Random(7)))
        assertEquals(deck, loadDeck(shuffled))
        assertEquals("s1-01", deck.first().id)
        assertEquals("s3-12", deck.last().id)
    }

    @Test
    fun idsAreUniqueAndTextIsNotBlank() {
        assertEquals(deck.size, deck.map { it.id }.toSet().size)
        assertTrue(deck.none { it.text.isBlank() })
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsIncompleteDeck() {
        loadDeck(Json.encodeToString(deck.drop(1)))
    }
}
