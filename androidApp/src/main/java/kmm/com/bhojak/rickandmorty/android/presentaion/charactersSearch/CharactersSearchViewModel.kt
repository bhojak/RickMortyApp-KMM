package kmm.com.bhojak.rickandmorty.android.presentaion.charactersSearch

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

data class CharactersSearchUiState(
    val searchedName:String = "",
    val characters: List<Character> = emptyList(),
    val isLoading:Boolean = false,
    val errorMessage:String? = null
)

sealed class CharactersSearchEvent {

    class OnChangeName(val name: String) : CharactersSearchEvent()

    data object OnDismissError : CharactersSearchEvent()

    data object OnLoadMore : CharactersSearchEvent()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharactersSearchViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel()  {

    private var totalPage:Int = INITIAL_PAGE
    private val _currentPage = MutableStateFlow(INITIAL_PAGE)

    private val _searchedName = MutableStateFlow("")
    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)


    val state = combine(_searchedName, _characters, _errorMessage, _isLoading) {
            name, characters, errorMessage, loading ->
        if (name.isBlank()) {
            _characters.update { emptyList() }
        }

        CharactersSearchUiState(
            searchedName = name,
            characters = characters,
            errorMessage = errorMessage,
            isLoading = loading

        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CharactersSearchUiState()
    )

    init {
        _searchedName
            .debounce(1000)
            .filter { name -> (name.isNotEmpty() && name.length > 1) }
            .distinctUntilChanged()
            .onEach {
                _isLoading.update { true }
                _currentPage.update { INITIAL_PAGE }
            }
            .flatMapLatest { name ->
                repository.getCharactersByName(name, _currentPage.value)
            }
            .onEach { _isLoading.update { false } }
            .onEach { result ->
                when(result) {
                    is Resource.Error -> {
                        _errorMessage.update { result.message }
                    }
                    is Resource.Success -> {
                        if (totalPage == INITIAL_PAGE) {
                            totalPage = result.data!!.info.pages
                        }
                        _characters.update { result.data?.characters ?: emptyList() }
                    }
                }
            }
            .launchIn(viewModelScope)


        _currentPage
            .debounce(300)
            .filter { page -> (page in (INITIAL_PAGE + 1)..totalPage) }
            .distinctUntilChanged()
            .onEach { _isLoading.update { true } }
            .flatMapLatest { page ->
                repository.getCharactersByName(_searchedName.value, page)
            }
            .onEach { _isLoading.update { false } }
            .onEach { result ->
                when(result) {
                    is Resource.Success -> {
                        _characters.update { it + result.data!!.characters }
                    }
                    is Resource.Error -> {
                        _errorMessage.update { result.message }
                    }

                }
            }
            .launchIn(viewModelScope)
    }


    fun onEvent(event: CharactersSearchEvent) {

        when(event) {

            is CharactersSearchEvent.OnChangeName -> {
                _searchedName.update { event.name }
            }

            is CharactersSearchEvent.OnDismissError -> {
                _errorMessage.update { null }
            }

            is CharactersSearchEvent.OnLoadMore -> {
                if (_currentPage.value < totalPage) {
                    _currentPage.update { it + 1 }
                }
            }
        }

    }



}