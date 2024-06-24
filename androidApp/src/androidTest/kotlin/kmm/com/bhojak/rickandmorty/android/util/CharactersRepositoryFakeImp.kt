package kmm.com.bhojak.rickandmorty.android.util

import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import kmm.com.bhojak.rickandmorty.android.api.entry.Characters
import kmm.com.bhojak.rickandmorty.android.domain.repository.CharactersRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CharactersRepositoryFakeImp(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CharactersRepository {

    override fun getCharacters(page: Int): Flow<Resource<Characters>> = flow {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Characters> = moshi.adapter(Characters::class.java)
        try {
            when (page) {
                1 -> emit(Resource.Success(jsonAdapter.fromJson(charactersJsonPage1)))
                2 -> emit(Resource.Success(jsonAdapter.fromJson(charactersJsonPage2)))
                3 -> emit(Resource.Success(jsonAdapter.fromJson(charactersJsonPage3)))
                4 -> throw Exception("No Internet Connection!")
                else -> throw Exception("Something got wrong!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(message = e.message!!))
        }

    }.flowOn(defaultDispatcher)


    override fun getCharactersByName(name: String, page: Int): Flow<Resource<Characters>> = getCharacters(page)
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    Resource.Success(
                        data = result.data!!.copy(
                            characters = result.data!!.characters.filter { it.name.lowercase().contains(name.lowercase()) }
                        )
                    )
                }
                else -> result
            }
        }.flowOn(defaultDispatcher)


    override suspend fun getCharacterById(id: Int): Resource<Character> = withContext(defaultDispatcher){

       return@withContext when(val result = getCharacters(1).last()){
            is Resource.Success -> {
                result.data!!.characters.firstOrNull { it.id == id }?.let {
                    Resource.Success(data = it)
                } ?: Resource.Error("Character not found!")
            }
            else -> Resource.Error(message = result.message!!)
        }


    }
}


