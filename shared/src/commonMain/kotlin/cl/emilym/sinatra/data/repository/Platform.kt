package cl.emilym.sinatra.data.repository

expect class PlatformContext

enum class Platform {
    ANDROID, IOS
}

data class PlatformInformation(
    val platform: Platform
)

expect val platform: PlatformInformation

val isIos: Boolean get() = platform.platform == Platform.IOS
val isAndroid: Boolean get() = platform.platform == Platform.ANDROID