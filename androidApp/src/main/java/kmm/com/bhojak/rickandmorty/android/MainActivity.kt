package kmm.com.bhojak.rickandmorty.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail.CharacterDetailEvent
import kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail.CharacterDetailViewModel
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersList.CharactersListViewModel
import kmm.com.bhojak.rickandmorty.android.presentaion.characterDetail.CharacterDetailScreen
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersList.CharactersListScreen
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersSearch.CharactersSearchScreen
import kmm.com.bhojak.rickandmorty.android.presentaion.charactersSearch.CharactersSearchViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RickAndMortyApp()
                }
            }
        }
    }
}

enum class RickAndMortyScreen(val title: String) {
    CharactersList(title = "Rick and Morty"),
    CharacterDetail(title = "Character Detail"),
    CharactersSearch(title = "Characters Search");

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RickAndMortyAppBar(
   currentScreen: RickAndMortyScreen,
   canNavigateBack: Boolean,
   navigateUp: () -> Unit,
   navigateTo: (RickAndMortyScreen) -> Unit,
   modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = currentScreen.title)
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back to the previous screen"
                    )
                }
            }
        },
        actions = {
            if (currentScreen == RickAndMortyScreen.CharactersList) {
                IconButton(onClick = { navigateTo(RickAndMortyScreen.CharactersSearch) }) {
                    Icon(
                        imageVector = Icons.Default.Search ,
                        contentDescription = "Search for characters"
                    )
                }
            }
        }
    )
}


@Composable
fun RickAndMortyApp(navController: NavHostController = rememberNavController()) {


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = RickAndMortyScreen.valueOf(
        backStackEntry?.destination?.route?.substringBefore("?") ?: RickAndMortyScreen.CharactersList.name
    )


    Scaffold(
        topBar = {
            if (currentScreen != RickAndMortyScreen.CharactersSearch) {
                RickAndMortyAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    navigateTo = {screen ->
                        navController.navigate(screen.name)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RickAndMortyScreen.CharactersList.name,
            modifier = Modifier.fillMaxSize()
        ) {

            composable(route = RickAndMortyScreen.CharactersList.name) {
                val viewModel = hiltViewModel<CharactersListViewModel>()
                CharactersListScreen(
                    state = viewModel.state.collectAsState().value,
                    onCharacterClick = { id ->
                        navController.navigate(route = "${RickAndMortyScreen.CharacterDetail.name}?characterId=$id")
                    },
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }


            composable(
                route = "${RickAndMortyScreen.CharacterDetail.name}?characterId={characterId}",
                arguments = listOf(navArgument("characterId") {
                    type = NavType.IntType
                })
            ) {backStackEntry ->
                val id = backStackEntry.arguments?.getInt("characterId")
                    ?: throw IllegalArgumentException("CharacterId can't be null.")

                val viewModel = hiltViewModel<CharacterDetailViewModel>()
                CharacterDetailScreen(
                    state = viewModel.state.collectAsState().value,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()

                )

                LaunchedEffect(Unit) {
                    viewModel.onEvent(CharacterDetailEvent.OnGetCharacterInfo(id))
                }
            }


            composable(route = RickAndMortyScreen.CharactersSearch.name) {
                val viewModel = hiltViewModel<CharactersSearchViewModel>()
                CharactersSearchScreen(
                    state = viewModel.state.collectAsState().value,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = { navController.navigateUp() },
                    onCharacterClick = { id ->
                        navController.navigate(route = "${RickAndMortyScreen.CharacterDetail.name}?characterId=$id")
                    } ,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
    }
}



