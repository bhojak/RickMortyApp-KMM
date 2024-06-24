package kmm.com.bhojak.rickandmorty.android.domain.repository

import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import kmm.com.bhojak.rickandmorty.android.api.entry.Characters
import kmm.com.bhojak.rickandmorty.android.util.Resource
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {

     fun getCharacters(page: Int) : Flow<Resource<Characters>>

     fun getCharactersByName(name: String, page: Int) : Flow<Resource<Characters>>

     suspend fun getCharacterById(id: Int) : Resource<Character>

}