package kmm.com.bhojak.rickandmorty.android.presentaion.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import kmm.com.bhojak.rickandmorty.android.domain.repository.CharactersRepository
import kmm.com.bhojak.rickandmorty.android.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val INITIAL_PAGE = 1

sealed class CharactersListEvent {
    data object OnLoadMore : CharactersListEvent()
    data object OnDismissError : CharactersListEvent()
}

data class CharactersListUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage:String? = null
)


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CharactersListViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel() {

    private var totalPage:Int = INITIAL_PAGE
    private  val _currentPage = MutableStateFlow(INITIAL_PAGE)

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)


    val state = combine(_characters, _errorMessage, _isLoading) {
        charactersList, errorMessage, isLoading ->

        CharactersListUiState(
            characters = charactersList,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CharactersListUiState()
    )


    init {
        _currentPage
            .debounce(300)
            .filter { page -> (page in INITIAL_PAGE..totalPage) }
            .distinctUntilChanged()
            .onEach { _isLoading.update { true } }
            .flatMapLatest { page ->
                repository.getCharacters(page = page)
            }
            .onEach { _isLoading.update { false } }
            .onEach { result ->
                when(result) {
                    is Resource.Success -> {
                        if (totalPage == INITIAL_PAGE) {
                            totalPage = result.data!!.info.pages
                        }
                        _characters.update { it + result.data!!.characters }
                    }
                    is Resource.Error -> {
                        _errorMessage.update { result.message }
                    }

                }
            }
            .launchIn(viewModelScope)
    }




    fun onEvent(event: CharactersListEvent) {
        when(event) {
            CharactersListEvent.OnDismissError -> {
                _errorMessage.update { null }
            }

            CharactersListEvent.OnLoadMore -> {
                if (_currentPage.value < totalPage) {
                    _currentPage.update { it + 1 }
                }
            }
        }
    }



}