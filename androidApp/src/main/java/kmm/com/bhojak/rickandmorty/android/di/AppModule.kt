package kmm.com.bhojak.rickandmorty.android.di

import android.content.Context
import kmm.com.bhojak.rickandmorty.android.api.ConnectionInterceptor
import kmm.com.bhojak.rickandmorty.android.api.RetrofitApi
import kmm.com.bhojak.rickandmorty.android.data.repository.CharactersRepositoryImp
import kmm.com.bhojak.rickandmorty.android.domain.repository.CharactersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesCharactersRepository(api: RetrofitApi) : CharactersRepository {
        return CharactersRepositoryImp(api = api)
    }


    @Provides
    @Singleton
    fun providesRetrofitApi(@ApplicationContext appContext: Context) : RetrofitApi {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(ConnectionInterceptor(appContext.applicationContext))
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(RetrofitApi::class.java)
    }


}