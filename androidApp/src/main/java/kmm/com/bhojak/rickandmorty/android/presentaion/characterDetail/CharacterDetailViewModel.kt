package kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import kmm.com.bhojak.rickandmorty.android.domain.repository.CharactersRepository
import kmm.com.bhojak.rickandmorty.android.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class CharacterDetailEvent {
    data object OnDismissError : CharacterDetailEvent()
    data class OnGetCharacterInfo(val id:Int) : CharacterDetailEvent()
}

data class CharacterDetailUiState(
    val character:Character? = null,
    val isLoading:Boolean = false,
    val errorMessage:String? = null
)


@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository:CharactersRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _character = MutableStateFlow<Character?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val state = combine(_isLoading, _character, _errorMessage) {
        isLoading, character, error ->
        CharacterDetailUiState(
            character = character,
            isLoading = isLoading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CharacterDetailUiState()
    )


    fun onEvent(event: CharacterDetailEvent) {

        when(event) {
            is CharacterDetailEvent.OnDismissError -> {
                _errorMessage.update { null }
            }

            is CharacterDetailEvent.OnGetCharacterInfo -> {
                viewModelScope.launch {
                    _isLoading.update { true }
                    when(val result = repository.getCharacterById(event.id)){
                        is Resource.Success -> {
                            _character.update { result.data }
                        }
                        is Resource.Error -> { result.message }
                    }
                    _isLoading.update { false }
                }
            }
        }

    }
}