package cl.emilym.sinatra.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cl.emilym.sinatra.ui.App
import cl.emilym.sinatra.ui.presentation.decompose.base.DefaultSinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.root.DefaultRootComponent
import cl.emilym.sinatra.ui.presentation.sharedScreenModule
import com.arkivanov.decompose.defaultComponentContext
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.mp.KoinPlatform

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootContext = DefaultSinatraComponentContext(
            defaultComponentContext(),
            KoinPlatform.getKoin()
        )
        val rootComponent = DefaultRootComponent(rootContext)
        setContent {
            App(rootComponent)
        }
    }
}