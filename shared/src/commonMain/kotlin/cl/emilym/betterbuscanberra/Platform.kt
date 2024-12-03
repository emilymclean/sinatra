package cl.emilym.betterbuscanberra

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform