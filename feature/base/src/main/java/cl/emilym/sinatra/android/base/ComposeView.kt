package cl.emilym.sinatra.android.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme

interface ComposeView {

    @Composable
    fun Content()

}

abstract class ComposeActivity: ComponentActivity(), ComposeView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SinatraTheme {
                Content()
            }
        }
    }

}