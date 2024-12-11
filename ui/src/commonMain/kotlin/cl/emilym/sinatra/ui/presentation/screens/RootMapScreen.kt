package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.ui.navigation.CurrentBottomSheetContent
import cl.emilym.sinatra.ui.navigation.CurrentMapOverlayContent
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapControl
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.isCurrentMapScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.MapSearchScreen
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigationItem
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_map
import sinatra.ui.generated.resources.navigation_bar_favourites

@Composable
expect fun Map(content: @Composable MapControl.(@Composable () -> Unit) -> Unit)

class RootMapScreen: Screen {

    @Composable
    override fun Content() {
        val sheetState = rememberSinatraBottomSheetState(
            initialValue = SinatraSheetValue.HalfExpanded,
            skipHiddenState = true
        )
        val scaffoldState = rememberSinatraBottomSheetScaffoldState(
            bottomSheetState = sheetState
        )
        Scaffold {
            if (isCurrentMapScreen()) {
                Map { map ->
                    CompositionLocalProvider(
                        LocalMapControl provides this,
                        LocalBottomSheetState provides scaffoldState
                    ) {
                        BottomSheet(scaffoldState) {
                            Box(Modifier.fillMaxSize()) {
                                map()
                                MapOverlay()
                            }
                        }
                    }
                }
            } else {
                CurrentScreen()
            }
        }
    }

    @Composable
    fun Scaffold(
        content: @Composable () -> Unit
    ) {
        Navigator(
            MapSearchScreen()
        ) { navigator ->
            val adaptiveWindowInfo = currentWindowAdaptiveInfo()

            val items = remember(navigator) {
                listOf(
                    NavigationItem(
                        { it is MapScreen },
                        {
                            navigator.popAll()
                            navigator.push(MapSearchScreen())
                        },
                        { MapIcon() },
                        { Text(stringResource(Res.string.navigation_bar_map)) }
                    ),
                    NavigationItem(
                        { it is FavouriteScreen },
                        {
                            navigator.popAll()
                            navigator.push(FavouriteScreen())
                        },
                        { StarOutlineIcon() },
                        { Text(stringResource(Res.string.navigation_bar_favourites)) }
                    )
                )
            }

            when (adaptiveWindowInfo.windowSizeClass.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT -> {
                    Column(Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f)) {
                            content()
                        }
                        NavigationBar {
                            for (item in items) {
                                with (item) {
                                    bar(navigator.lastItem)
                                }
                            }
                        }
                    }
                }
                else -> {
                    Row(Modifier.fillMaxSize()) {
                        NavigationRail {
                            for (item in items) {
                                with (item) {
                                    rail(navigator.lastItem)
                                }
                            }
                        }
                        Box(Modifier.weight(1f)) {
                            content()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomSheet(
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