package cl.emilym.sinatra.data.repository

import android.content.Context

actual val platform: PlatformInformation = PlatformInformation(
    platform = Platform.ANDROID
)

actual class PlatformContext(
    val context: Context
)