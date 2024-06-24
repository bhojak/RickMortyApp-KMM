package kmm.com.bhojak.android.presentation.charactersList

import app.cash.turbine.test
import kmm.com.bhojak.android.util.CharactersRepositoryFakeImp
import kmm.com.bhojak.android.util.MainDispatcherRule
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersList.CharactersListEvent
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersList.CharactersListViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharactersListViewModelTest {

    private lateinit var viewModel: CharactersListViewModel


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Before
    fun setUp() {
        viewModel = CharactersListViewModel(
            repository = CharactersRepositoryFakeImp(
                defaultDispatcher = mainDispatcherRule.testDispatcher
            )
        )
    }


    @Test
    fun `When the ViewModel started it should return the first 20 characters of the list`() = runTest {

        viewModel.state.test {

           // Initial state
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(0, characters.size)
            }

            // Should start Loading
            with(awaitItem()) {
                assertEquals(true, isLoading)
                assertEquals(0, characters.size)
            }

            // Should Finish Loading
            with(awaitItem()){
                assertEquals(false, isLoading)
                assertEquals(20, characters.size)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }
    }



    @Test
    fun `When call for OnLoadMore the ViewModel should update the list count to 40 items`() = runTest {

        viewModel.state.test {

            // Given the idle state.
            skipItems(2)
            with(awaitItem()){
                assertEquals(false, isLoading)
                assertEquals(20, characters.size)
            }

            // Do a call for more items
            viewModel.onEvent( CharactersListEvent.OnLoadMore )


            // Then check the final state
            skipItems(1)
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(40, characters.size)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `When request for the fourth page the viewModel should get a error message`() = runTest {

        viewModel.state.test {

            //Ignore the last 3 emissions (Initial state)
            skipItems(3)

            // Scroll for more 2 pages
            repeat(2) {
                viewModel.onEvent( CharactersListEvent.OnLoadMore )
                skipItems(2)
            }

            // Call for the fourth page (Should cause a mock error)
            viewModel.onEvent( CharactersListEvent.OnLoadMore )
            skipItems(1)

            // Finish state
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(60, characters.size)
                assertEquals("No Internet Connection!", errorMessage)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }

    }
}