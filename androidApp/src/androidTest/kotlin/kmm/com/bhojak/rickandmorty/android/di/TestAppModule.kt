package kmm.com.bhojak.rickandmorty.android.di

import kmm.com.bhojak.rickandmorty.android.util.CharactersRepositoryFakeImp
import kmm.com.bhojak.rickandmorty.android.domain.repository.CharactersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideFakeCharactersRepository() : CharactersRepository {
        return CharactersRepositoryFakeImp()
    }


}