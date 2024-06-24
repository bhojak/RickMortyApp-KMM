package kmm.com.bhojak.rickandmorty

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform