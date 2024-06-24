package kmm.com.bhojak.android.presentation.charactersSearch

import app.cash.turbine.test
import kmm.com.bhojak.android.util.CharactersRepositoryFakeImp
import kmm.com.bhojak.android.util.MainDispatcherRule
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersSearch.CharactersSearchEvent
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersSearch.CharactersSearchViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharactersSearchViewModelTest {

    private lateinit var viewModel:CharactersSearchViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        viewModel = CharactersSearchViewModel(
            repository = CharactersRepositoryFakeImp(
                defaultDispatcher = mainDispatcherRule.testDispatcher
            )
        )
    }


    @Test
    fun `When the ViewModel started it should return no items in the list`() = runTest {

        viewModel.state.test {

            // Initial state
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(0, characters.size)
                assertEquals("", searchedName)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `When search for the name 'rick' the ViewModel should return some results`() = runTest {

        viewModel.state.test {

            // Initial state
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(0, characters.size)
                assertEquals("", searchedName)
            }

            // Search for 'rick'
            viewModel.onEvent( CharactersSearchEvent.OnChangeName(name = "rick"))
            skipItems(1) // skipping the change on the searchedName.

            // Starting loading
            with(awaitItem()) {
                assertEquals(true, isLoading)
                assertEquals(0, characters.size)
                assertEquals("rick", searchedName)
            }

            // Check the final state
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(4, characters.size)
                assertEquals("rick", searchedName)
            }


            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }

    }


}