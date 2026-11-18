package com.tom.closercards.model

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckRepositoryTest {
    // Unit tests run with the app module as the working directory.
    private val bundled = DeckRepository.parse(File("src/main/assets/cards.json").readText())

    @Test
    fun bundledDeckHasThreeSetsOfTwelveInOrder() {
        assertEquals(listOf(1, 2, 3), bundled.sets.map { it.number })
        bundled.sets.forEach { set ->
            assertEquals(12, set.cards.size)
            assertEquals((1..12).toList(), set.cards.map { it.order })
            assertTrue(set.cards.all { it.set == set.number })
        }
        assertEquals(36, bundled.sets.flatMap { it.cards }.map { it.id }.toSet().size)
    }

    @Test
    fun onlyTheLastSetCarriesThePassNote() {
        assertNull(bundled.sets[0].passNote)
        assertNull(bundled.sets[1].passNote)
        assertEquals("Either of you may pass on any question.", bundled.sets[2].passNote)
    }

    @Test
    fun sortsByOrderAndSetNumberWhateverTheFileOrder() {
        val deck = DeckRepository.parse(
            """
            {"sets":[{"set":2,"title":"B","guidance":"g"},{"set":1,"title":"A","guidance":"g"}],
             "cards":[{"id":"b","set":1,"order":2,"text":"second"},{"id":"c","set":2,"order":1,"text":"third"},
                      {"id":"a","set":1,"order":1,"text":"first"}]}
            """,
        )
        assertEquals(listOf("A", "B"), deck.sets.map { it.title })
        assertEquals(listOf("first", "second"), deck.sets[0].cards.map { it.text })
    }

    @Test
    fun rejectsBrokenDecks() {
        val set = """{"set":1,"title":"A","guidance":"g"}"""
        fun deck(cards: String, sets: String = set) = """{"sets":[$sets],"cards":[$cards]}"""
        val broken = listOf(
            deck("""{"id":"a","set":1,"order":1,"text":"x"},{"id":"a","set":1,"order":2,"text":"y"}"""),
            deck("""{"id":"a","set":2,"order":1,"text":"x"}"""),
            deck("""{"id":"a","set":1,"order":1,"text":"x"},{"id":"b","set":1,"order":1,"text":"y"}"""),
            deck("""{"id":"a","set":1,"order":1,"text":" "}"""),
            deck("", sets = """{"set":1,"title":"A","guidance":"g"}"""),
            deck("""{"id":"a","set":2,"order":1,"text":"x"}""", sets = """{"set":2,"title":"A","guidance":"g"}"""),
        )
        broken.forEach { json -> assertThrows(IllegalArgumentException::class.java) { DeckRepository.parse(json) } }
    }
}
