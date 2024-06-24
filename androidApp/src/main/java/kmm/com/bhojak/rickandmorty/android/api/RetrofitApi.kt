package kmm.com.bhojak.rickandmorty.android.api

import kmm.com.bhojak.rickandmorty.android.api.entry.Character
import kmm.com.bhojak.rickandmorty.android.api.entry.Characters
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitApi {

    @GET("character")
    suspend fun characters(@Query("page") page: Int) : Characters

    @GET("character")
    suspend fun charactersByName(@Query("name") name:String, @Query("page") page: Int) : Characters

    @GET("character/{id}")
    suspend fun characterById(@Path("id") id:Int) : Character

}
