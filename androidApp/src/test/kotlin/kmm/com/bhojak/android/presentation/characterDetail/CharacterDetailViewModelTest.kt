package kmm.com.bhojak.android.presentation.characterDetail

import app.cash.turbine.test
import kmm.com.bhojak.android.util.CharactersRepositoryFakeImp
import kmm.com.bhojak.android.util.MainDispatcherRule
import kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail.CharacterDetailEvent
import kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail.CharacterDetailViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterDetailViewModelTest {

    private lateinit var viewModel: CharacterDetailViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Before
    fun setUp() {
        viewModel = CharacterDetailViewModel(
            repository = CharactersRepositoryFakeImp(
                defaultDispatcher = mainDispatcherRule.testDispatcher
            )
        )
    }


    @Test
    fun `When call for 'OnLoadCharacterInfo' ViewModel  should return a valid Character object`() = runTest {

        viewModel.state.test {

            // Initial State
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertEquals(null, character)
            }

            // Call for the Character info
            viewModel.onEvent( CharacterDetailEvent.OnGetCharacterInfo(id = 1) )

            // Loading State
            with(awaitItem()) {
                assertEquals(true, isLoading)
                assertEquals(null, character)
            }

            // Final State
            with(awaitItem()) {
                assertEquals(false, isLoading)
                assertNotNull(character)
                assertEquals(1, character?.id)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }

    }

}