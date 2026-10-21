package com.tom.closercards

import com.tom.closercards.model.Card
import com.tom.closercards.model.loadDeck
import com.tom.closercards.model.loadDeckInfos
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

    @Test
    fun everyDeckInDecksJsonLoads() {
        val infos = loadDeckInfos(File("src/main/assets/decks.json").readText())
        assertEquals(listOf("main", "after-dark", "after-darker"), infos.map { it.id })
        assertEquals("cards.json", infos.first().file)
        val ids = infos.flatMap { loadDeck(File("src/main/assets/${it.file}").readText()) }.map { it.id }
        assertEquals("card ids must be unique across decks", ids.size, ids.toSet().size)
        assertTrue("main deck has no consent screen", infos.first().consent == null)
        assertTrue("adult decks need a consent screen", infos.drop(1).all { !it.consent.isNullOrBlank() })
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDeckInfoWithoutAllThemes() {
        loadDeckInfos("""[{"id":"x","title":"X","file":"x.json","themes":["a"],"guidance":["a","b","c"]}]""")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsIncompleteDeck() {
        loadDeck(Json.encodeToString(deck.drop(1)))
    }
}
