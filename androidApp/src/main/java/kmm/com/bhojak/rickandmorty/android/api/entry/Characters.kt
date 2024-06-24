package kmm.com.bhojak.rickandmorty.android.api.entry

import com.squareup.moshi.Json

data class Characters(
    val info: PageInfo,
    @field:Json(name="results")
    val characters: List<Character>
)