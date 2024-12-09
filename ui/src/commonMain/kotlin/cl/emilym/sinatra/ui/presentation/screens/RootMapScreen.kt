package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.navigation.CurrentBottomSheetContent
import cl.emilym.sinatra.ui.navigation.CurrentMapOverlayContent
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.presentation.screens.maps.MapSearchScreen
import cl.emilym.sinatra.ui.widgets.bottomsheet.NotShitSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.StupidBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberNotShitBottomSheetState
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberStupidBottomSheetScaffoldState

@Composable
expect fun Map()

class RootMapScreen: Screen {

    @Composable
    override fun Content() {
        Navigator(
            MapSearchScreen()
        ) {
            Scaffold {
                Box(Modifier.fillMaxSize()) {
                    Map()
                    MapOverlay()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Scaffold(
        content: @Composable () -> Unit
    ) {
        val sheetState = rememberNotShitBottomSheetState(
            initialValue = NotShitSheetValue.HalfExpanded,
            skipHiddenState = true
        )
        val state = rememberStupidBottomSheetScaffoldState(
            bottomSheetState = sheetState
        )

        StupidBottomSheetScaffold(
            scaffoldState = state,
            sheetContent = {
                CompositionLocalProvider(LocalBottomSheetState provides state) {
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