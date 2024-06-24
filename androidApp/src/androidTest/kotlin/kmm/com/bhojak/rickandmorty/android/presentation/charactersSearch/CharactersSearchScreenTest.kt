package kmm.com.bhojak.rickandmorty.android.presentation.charactersSearch

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import kmm.com.bhojak.rickandmorty.android.MainActivity
import kmm.com.bhojak.rickandmorty.android.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class)
class CharactersSearchScreenTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun shouldSearchForASpecificCharacterAndOpenItsDetailScreen() = runTest {

        composeTestRule
            .waitUntilExactlyOneExists(
                hasContentDescription("Character named Rick Sanchez, is a Human, with id equals 1")
            )

        composeTestRule
            .onNodeWithContentDescription("Search for characters")
            .performClick()

        composeTestRule
            .waitUntilExactlyOneExists(
                hasContentDescription("Enter the character name to start the search")
            )

        composeTestRule
            .onNodeWithContentDescription("Enter the character name to start the search")
            .performTextInput("rick")

        composeTestRule
            .waitUntilExactlyOneExists(
                hasContentDescription("Character named Rick Sanchez, is a Human, with id equals 1")
            )

        composeTestRule
            .onNodeWithContentDescription("Character named Alien Rick, is a Alien, with id equals 15")
            .performClick()

        composeTestRule
            .waitUntilExactlyOneExists(
                hasContentDescription("Character named Alien Rick is unknown")
            )
    }


}