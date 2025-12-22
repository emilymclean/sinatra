package cl.emilym.sinatra.ui.presentation.decompose.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.invisibleToUser
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.ui.maps.rememberMapControl
import cl.emilym.sinatra.ui.presentation.screens.Map
import cl.emilym.sinatra.ui.widgets.ViewportSizeWidget
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.value

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainComponentContentPage(
    component: MainComponent
) {
    val mapControl = rememberMapControl()
    ViewportSizeWidget {
        Box(Modifier.fillMaxSize()) {
            Map(
                mapControl,
                component.mapItems.collectAsStateWithLifecycle().value,
                Modifier
                    .fillMaxSize()
                    .then(
                        if (FeatureFlag.HIDE_MAPS_FROM_ACCESSIBILITY.value())
                            Modifier.clearAndSetSemantics {
                                invisibleToUser()
                            }
                        else Modifier
                    )
            )
        }
    }
}