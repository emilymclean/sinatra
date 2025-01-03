package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.navigation.CurrentBottomSheetContent
import cl.emilym.sinatra.ui.navigation.CurrentMapOverlayContent
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScreen
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.isCurrentMapScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchScreen
import cl.emilym.sinatra.ui.widgets.InfoIcon
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.LocalViewportSize
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigationItem
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetState
import cl.emilym.sinatra.ui.widgets.toFloatPx
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_about
import sinatra.ui.generated.resources.navigation_bar_favourites
import sinatra.ui.generated.resources.navigation_bar_map

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
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val height = maxHeight.toFloatPx()
                val width = maxWidth.toFloatPx()

                CompositionLocalProvider(
                    LocalBottomSheetState provides scaffoldState,
                    LocalViewportSize provides Size(width, height)
                ) {
                    if (isCurrentMapScreen()) {
                        Map { map ->
                            CompositionLocalProvider(
                                LocalMapControl provides this
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
            var selected by rememberSaveable { mutableStateOf(0) }
            val selectedCallback: (Int) -> Unit = remember { { selected = it } }

            val items = remember(navigator) {
                listOf(
                    NavigationItem(
                        0,
                        {
                            navigator.replaceAll(MapSearchScreen())
                        },
                        { MapIcon() },
                        { Text(stringResource(Res.string.navigation_bar_map)) }
                    ),
                    NavigationItem(
                        1,
                        {
                            navigator.replaceAll(FavouriteScreen())
                        },
                        { StarOutlineIcon() },
                        { Text(stringResource(Res.string.navigation_bar_favourites)) }
                    ),
                    NavigationItem(
                        2,
                        {
                            navigator.replaceAll(AboutScreen())
                        },
                        { InfoIcon() },
                        { Text(stringResource(Res.string.navigation_bar_about)) }
                    )
                )
            }

            when (adaptiveWindowInfo.windowSizeClass.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT -> {
                    Column(Modifier.fillMaxSize()) {
                        Box(
                            Modifier.weight(1f)
                                .consumeWindowInsets(WindowInsets.safeContent.only(WindowInsetsSides.Bottom))
                        ) {
                            content()
                        }
                        NavigationBar {
                            for (item in items) {
                                with (item) {
                                    bar(selected, selectedCallback = selectedCallback)
                                }
                            }
                        }
                    }
                }
                else -> {
                    Row(Modifier.fillMaxSize()) {
                        Box(
                            Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(WindowInsets.displayCutout.only(WindowInsetsSides.Start).asPaddingValues())
                        ) {
                            NavigationRail(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ) {
                                for (item in items) {
                                    with (item) {
                                        rail(selected, selectedCallback = selectedCallback)
                                    }
                                }
                            }
                        }
                        Box(
                            Modifier
                                .weight(1f)
                                .consumeWindowInsets(WindowInsets.displayCutout.only(WindowInsetsSides.Start))
                        ) {
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
            sheetHalfHeight = bottomSheetHalfHeight(),
            sheetContainerColor = MaterialTheme.colorScheme.background
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