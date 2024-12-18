package cl.emilym.sinatra

data class BuildInformation(
    val versionName: String,
    val versionNumber: String,
    val nominatimUserAgent: String,
    val nominatimEmail: String,
)