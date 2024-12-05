package cl.emilym.sinatra.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.emilym.sinatra.ui.presentation.sharedScreenModule
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cl.emilym.sinatra.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // We set up the screen registry here so we could potentially launch into a purely android view (i.e if we needed to do something with webview?)
        ScreenRegistry {
            sharedScreenModule()
        }

        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}