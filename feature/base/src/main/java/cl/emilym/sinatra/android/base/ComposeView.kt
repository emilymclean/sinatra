package cl.emilym.sinatra.android.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import cl.emilym.sinatra.ui.localization.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.presentation.screens.AppOutOfDateScreen
import cl.emilym.sinatra.ui.presentation.screens.RootMapScreen
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme
import cl.emilym.sinatra.ui.widgets.LocalPermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueueHandler

interface ComposeView {

    @Composable
    fun Content()

}

abstract class ComposeActivity: ComponentActivity(), ComposeView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val permissionQueue = remember { PermissionRequestQueue() }

            SinatraTheme {
                CompositionLocalProvider(
                    LocalPermissionRequestQueue provides permissionQueue
                ) {
                    Content()
                    PermissionRequestQueueHandler()
                }
            }
        }
    }

}