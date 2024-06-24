package kmm.com.bhojak.rickandmorty.android.presentaion.charactersList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import coil.compose.AsyncImage

private const val BUFFER = 5

@Composable
fun CharactersListScreen(
    state: CharactersListUiState,
    onEvent: (CharactersListEvent) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.let {
                lastVisibleItem.index != 0 && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - BUFFER
            } ?: false

        }
    }

    LaunchedEffect(reachedBottom) {
        if(reachedBottom) onEvent( CharactersListEvent.OnLoadMore )
    }

    LazyColumn(
        state = listState,
        modifier = modifier.semantics {
            contentDescription = "List of characters"
        },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(state.characters) { character ->
            CharacterListItemView(
                character = character,
                onClick = { onCharacterClick(character.id) }
            )
        }
        if (state.isLoading) {
            item {
                Box (modifier = Modifier
                    .semantics {
                        contentDescription = "Screen is current loading"
                    }
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }

            }
        }
    }
}



@Composable
fun CharacterListItemView(
    modifier: Modifier = Modifier,
    character: Character,
    onClick : () -> Unit
) {
    Card (
        modifier = modifier
            .semantics {
                contentDescription = "Character named ${character.name}, is a ${character.species}, with id equals ${character.id}"
            }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { onClick() }
        ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(corner = CornerSize(8.dp))

    ) {
        Row {
            CharacterImageView(character = character)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = character.name, style =  MaterialTheme.typography.titleMedium)
                Text(text = character.species, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun CharacterImageView(
    character: Character
) {
    AsyncImage(
        model = character.image,
        contentDescription = "${character.name} photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(CornerSize(16.dp)))

    )
}