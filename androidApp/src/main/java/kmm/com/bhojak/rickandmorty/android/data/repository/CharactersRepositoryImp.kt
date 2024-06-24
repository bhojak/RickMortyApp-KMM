package kmm.com.bhojak.rickandmorty.android.data.repository

import kmm.com.bhojak.rickandmorty.android.api.RetrofitApi
import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import kmm.com.bhojak.rickandmorty.android.api.entry.Characters
import kmm.com.bhojak.rickandmorty.android.api.safeApiCall
import kmm.com.bhojak.rickandmorty.android.domain.repository.CharactersRepository
import kmm.com.bhojak.rickandmorty.android.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CharactersRepositoryImp @Inject constructor(
    private val api:RetrofitApi,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

) : CharactersRepository {

    override fun getCharacters(page:Int): Flow<Resource<Characters>> = flow  {
        emit( safeApiCall(defaultDispatcher) { api.characters(page) } )
    }

    override fun getCharactersByName(name: String, page: Int): Flow<Resource<Characters>> = flow {
        emit( safeApiCall(defaultDispatcher) { api.charactersByName(name, page)})
    }

    override suspend fun getCharacterById(id: Int): Resource<Character> = safeApiCall(defaultDispatcher) {
        api.characterById(id)
    }


}