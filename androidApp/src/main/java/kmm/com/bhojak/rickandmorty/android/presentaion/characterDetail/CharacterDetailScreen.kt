package kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun CharacterDetailScreen(
    state: CharacterDetailUiState,
    onEvent: (CharacterDetailEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        state.character?.let { character ->
            item {
                CharacterNameTitleView(
                    modifier = Modifier.semantics {
                        contentDescription = "Character named ${character.name} is ${character.status}"
                    },
                    name = character.name,
                    status = character.status
                )
                Spacer(modifier = Modifier.height(8.dp))
            }


            item {
                AsyncImage(
                    model = character.image,
                    contentDescription = "${character.name} image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DetailDescriptionView(
                    label = "Last known location",
                    description = character.location.name
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DetailDescriptionView(
                    label = "Species",
                    description = character.species
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DetailDescriptionView(
                    label = "Gender",
                    description = character.gender
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DetailDescriptionView(
                    label = "Origin",
                    description = character.origin.name
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DetailDescriptionView(
                    label = "Episode count",
                    description = "${character.episode.size}"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CharacterNameTitleView(
    name: String,
    status:String,
    modifier: Modifier = Modifier
) {
    val color = status.let {
        return@let when(it.lowercase()) {
           "alive" -> Color.Green
            "dead" -> Color.Red
            else -> Color.Yellow
       }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Status: $status",
            fontSize = 20.sp,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = color,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Text(
            text = name,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 50.sp
        )
    }
}



@Composable
fun DetailDescriptionView(
    label: String,
    description: String,
    modifier:Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = description,
            fontSize = 24.sp,
            color = Color.DarkGray
        )
    }
}