package com.tom.closercards

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tom.closercards.model.loadDeck
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullSessionTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun str(@StringRes id: Int, vararg args: Any) = rule.activity.getString(id, *args)

    private fun click(@StringRes id: Int) = rule.onNodeWithText(str(id)).performScrollTo().performClick()

    @Test
    fun playsFromHomeToSessionCompleteAndAgain() {
        val deck = loadDeck(rule.activity.assets.open("cards.json").bufferedReader().use { it.readText() })

        click(R.string.start)
        click(R.string.start_set_1) // names left blank, so defaults apply

        for (set in 1..3) {
            click(R.string.begin_set)
            for (order in 1..12) {
                rule.onNodeWithText(str(R.string.progress, set, order, 12)).assertIsDisplayed()
                val first = str(if (order % 2 == 1) R.string.default_a else R.string.default_b)
                rule.onNodeWithText(str(R.string.turn_banner, first)).assertIsDisplayed()

                if (order % 4 == 0) {
                    click(R.string.skip) // skip without flipping
                } else {
                    rule.onNodeWithTag("card").performScrollTo().performClick()
                    rule.onNodeWithText(deck[(set - 1) * 12 + order - 1].text).assertIsDisplayed()
                    click(R.string.next)
                }
            }
            if (set < 3) {
                rule.onNodeWithText(str(R.string.set_complete_title, set)).assertIsDisplayed()
                click(R.string.continue_set)
            }
        }

        rule.onNodeWithText(str(R.string.done_title)).assertIsDisplayed()
        click(R.string.play_again)
        rule.onNodeWithText(str(R.string.setup_title)).assertIsDisplayed()
    }
}
