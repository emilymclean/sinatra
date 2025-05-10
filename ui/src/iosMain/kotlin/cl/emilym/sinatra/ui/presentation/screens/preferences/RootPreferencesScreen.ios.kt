package cl.emilym.sinatra.ui.presentation.screens.preferences

import cl.emilym.sinatra.data.repository.PlatformContext
import platform.Foundation.NSURL.Companion.URLWithString
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual fun openLocationSettings(platformContext: PlatformContext) {
    UIApplication.sharedApplication.openURL(URLWithString(UIApplicationOpenSettingsURLString)!!)
}