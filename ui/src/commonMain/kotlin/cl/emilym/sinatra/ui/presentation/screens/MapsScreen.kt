package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.ui.maps.LocalMapStackKey
import cl.emilym.sinatra.ui.maps.LocalMapsManager
import cl.emilym.sinatra.ui.maps.generateMapObjectKey
import cl.emilym.sinatra.ui.widgets.OnPopEffect

fun generateBottomSheetScreenKey() = generateMapObjectKey()
fun generateGoogleMapsStackKey() = generateMapObjectKey()

typealias MapStackKey = String

abstract class MapsScreen: Screen {
    abstract val needsMapHandle: Boolean

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        if (needsMapHandle) {
            val mapManager = LocalMapsManager.current
            mapManager.push(key)
            OnPopEffect(key) {
                mapManager.pop(key)
            }
        }

        val cwi = ScaffoldDefaults.contentWindowInsets
        val insets = remember(cwi) {
            MutableWindowInsets(cwi)
        }
        Box(
            Modifier.onConsumedWindowInsetsChanged { consumedWindowInsets ->
                insets.insets = cwi.exclude(consumedWindowInsets)
            }.fillMaxSize().padding(insets.insets.asPaddingValues())
        ) {
            CompositionLocalProvider(
                LocalMapStackKey provides if (needsMapHandle) key else null
            ) {
                MainContent()
            }
        }
    }

    @Composable
    abstract fun MainContent()
}