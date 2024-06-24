package kmm.com.bhojak.rickandmorty.android.presentaion.charactersSearch

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersList.CharacterListItemView

private const val BUFFER = 1

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CharactersSearchScreen(
    state: CharactersSearchUiState,
    onEvent: (CharactersSearchEvent) -> Unit,
    onNavigateBack : () -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.let {
                lastVisibleItem.index != 0 && lastVisibleItem.index == listState.layoutInfo.totalItemsCount - BUFFER
            } ?: false

        }
    }

    LaunchedEffect(reachedBottom) {
        if(reachedBottom) onEvent( CharactersSearchEvent.OnLoadMore )
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        stickyHeader {
            TopAppBar(
                title = {
                    TextField(
                        modifier = Modifier
                            .semantics { contentDescription = "Enter the character name to start the search" }
                            .fillMaxWidth(),
                        value = state.searchedName,
                        onValueChange = { onEvent(CharactersSearchEvent.OnChangeName(it)) },
                        singleLine = true,
                        placeholder = {
                            Text(
                                modifier = Modifier.alpha(0.5f),
                                text = "Character name",
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            cursorColor = Color.Black.copy(alpha = 0.5f),
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent


                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to the previous screen"
                        )
                    }

                },
                actions = {
                    IconButton(onClick = { onEvent(CharactersSearchEvent.OnChangeName("")) }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Button"
                        )
                    }
                },

            )
        }

        items(state.characters) { character ->
            CharacterListItemView(
                character = character,
                onClick = { onCharacterClick(character.id) }
            )
        }
        if (state.isLoading) {
            item {
                Box (modifier = Modifier
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }

            }
        }

    }
}
