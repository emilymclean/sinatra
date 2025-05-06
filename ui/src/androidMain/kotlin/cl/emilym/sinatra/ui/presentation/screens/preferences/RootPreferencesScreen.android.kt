package cl.emilym.sinatra.ui.presentation.screens.preferences

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import cl.emilym.sinatra.data.repository.PlatformContext

actual fun openLocationSettings(platformContext: PlatformContext) {
    platformContext.context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", platformContext.context.packageName,  null)
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}