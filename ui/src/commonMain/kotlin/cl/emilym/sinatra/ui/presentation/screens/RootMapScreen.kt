package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.ui.navigation.CurrentBottomSheetContent
import cl.emilym.sinatra.ui.navigation.CurrentMapOverlayContent
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapControl
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.presentation.screens.maps.MapSearchScreen
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch

@Composable
expect fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit)

class RootMapScreen: Screen {

    @Composable
    override fun Content() {
        val permissionsFactory = rememberPermissionsControllerFactory()
        val permissionsController: PermissionsController = remember(permissionsFactory) {
            permissionsFactory.createPermissionsController()
        }
        BindEffect(permissionsController)

        val sheetState = rememberSinatraBottomSheetState(
            initialValue = SinatraSheetValue.HalfExpanded,
            skipHiddenState = true
        )
        val scaffoldState = rememberSinatraBottomSheetScaffoldState(
            bottomSheetState = sheetState
        )

        Navigator(
            MapSearchScreen()
        ) {
            Map { map ->
                CompositionLocalProvider(
                    LocalMapControl provides this,
                    LocalBottomSheetState provides scaffoldState
                ) {
                    Scaffold(scaffoldState) {
                        Box(Modifier.fillMaxSize()) {
                            map()
                            MapOverlay()
                        }
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Scaffold(
        scaffoldState: SinatraBottomSheetScaffoldState,
        content: @Composable () -> Unit
    ) {
        SinatraBottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                val coroutineScope = rememberCoroutineScope()
                SinatraBackHandler(scaffoldState.bottomSheetState.targetValue == SinatraSheetValue.Expanded) {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.halfExpand()
                    }
                }
                CompositionLocalProvider(LocalBottomSheetState provides scaffoldState) {
                    CurrentBottomSheetContent()
                }
            },
            sheetHalfHeight = bottomSheetHalfHeight()
        ) {
            content()
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun MapOverlay() {
        val cwi = ScaffoldDefaults.contentWindowInsets
        val insets = remember(cwi) {
            MutableWindowInsets(cwi)
        }
        Box(
            Modifier.onConsumedWindowInsetsChanged { consumedWindowInsets ->
                insets.insets = cwi.exclude(consumedWindowInsets)
            }.fillMaxSize().padding(insets.insets.asPaddingValues())
        ) {
            CurrentMapOverlayContent()
        }
    }

}