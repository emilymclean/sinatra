package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.compose.units.px
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.ui.maps.MapControl
import cl.emilym.sinatra.ui.maps.rememberMapControl
import cl.emilym.sinatra.ui.navigation.CurrentBottomSheetContent
import cl.emilym.sinatra.ui.navigation.CurrentMapOverlayContent
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.navigation.isCurrentMapScreen
import cl.emilym.sinatra.ui.plus
import cl.emilym.sinatra.ui.presentation.screens.content.MoreScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigateEntryScreen
import cl.emilym.sinatra.ui.presentation.screens.maps.search.MapSearchScreen
import cl.emilym.sinatra.ui.widgets.InfoIcon
import cl.emilym.sinatra.ui.widgets.JourneyIcon
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigationItem
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.ViewportSizeWidget
import cl.emilym.sinatra.ui.widgets.WarningIcon
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetState
import cl.emilym.sinatra.ui.widgets.value
import cl.emilym.sinatra.ui.widgets.viewportHeight
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_favourites
import sinatra.ui.generated.resources.navigation_bar_map
import sinatra.ui.generated.resources.navigation_bar_more
import sinatra.ui.generated.resources.navigation_bar_navigate
import sinatra.ui.generated.resources.service_alert_title

@Composable
expect fun Map(
    mapControl: MapControl,
    modifier: Modifier = Modifier
)

@get:Composable
@OptIn(ExperimentalComposeUiApi::class)
val mapModifier get() = Modifier
    .fillMaxSize()
    .then(
        if (FeatureFlag.HIDE_MAPS_FROM_ACCESSIBILITY.value())
            Modifier.clearAndSetSemantics {
                invisibleToUser()
            }
        else Modifier
    )

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
            ViewportSizeWidget {
                val adaptiveWindowInfo = currentWindowAdaptiveInfo()

                CompositionLocalProvider(
                    LocalBottomSheetState provides scaffoldState
                ) {
                    if (isCurrentMapScreen()) {
                        val mapControl = rememberMapControl()
                        CompositionLocalProvider(
                            LocalMapControl provides mapControl
                        ) {
                            when (adaptiveWindowInfo.windowSizeClass.windowWidthSizeClass) {
                                WindowWidthSizeClass.COMPACT -> {
                                    BottomSheet(scaffoldState) {
                                        ViewportSizeWidget {
                                            Map(
                                                mapControl,
                                                mapModifier
                                            )
                                            MapOverlay()
                                        }
                                    }
                                }
                                else -> {
                                    Row(
                                        Modifier
                                            .fillMaxSize()
                                    ) {
                                        Box(
                                            Modifier
                                                .background(MaterialTheme.colorScheme.surface)
                                                .fillMaxHeight()
                                                .widthIn(max = 740.dp)
                                                .fillMaxWidth(0.5f)
                                        ) {
                                            CompositionLocalProvider(
                                                LocalContentColor provides MaterialTheme.colorScheme.onSurface
                                            ) {
                                                CurrentBottomSheetContent()
                                            }
                                        }
                                        ViewportSizeWidget {
                                            Map(
                                                mapControl,
                                                mapModifier
                                            )
                                            MapOverlay()
                                        }
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

            val navigateTabBar = FeatureFlag.NAVIGATE_BUTTON_TAB_BAR.value()
            val serviceAlertTabBar = FeatureFlag.SERVICE_ALERT_BUTTON_TAB_BAR.value()
            val items = remember(
                navigator,
                navigateTabBar,
                serviceAlertTabBar
            ) {
                var index = 0
                listOfNotNull(
                    NavigationItem(
                        index++,
                        {
                            navigator.replaceAll(MapSearchScreen())
                        },
                        { MapIcon() },
                        { Text(stringResource(Res.string.navigation_bar_map)) }
                    ),
                    if (navigateTabBar) {
                        NavigationItem(
                            index++,
                            {
                                navigator.replaceAll(NavigateEntryScreen(null, null))
                            },
                            { JourneyIcon() },
                            { Text(stringResource(Res.string.navigation_bar_navigate)) }
                        )
                    } else null,
                    NavigationItem(
                        index++,
                        {
                            navigator.replaceAll(FavouriteScreen())
                        },
                        { StarOutlineIcon() },
                        { Text(stringResource(Res.string.navigation_bar_favourites)) }
                    ),
                    if (serviceAlertTabBar) {
                        NavigationItem(
                            index++,
                            {
                                navigator.replaceAll(ServiceAlertScreen())
                            },
                            { WarningIcon() },
                            { Text(stringResource(Res.string.service_alert_title)) }
                        )
                    } else null,
                    NavigationItem(
                        index++,
                        {
                            navigator.replaceAll(MoreScreen())
                        },
                        { InfoIcon() },
                        { Text(stringResource(Res.string.navigation_bar_more)) }
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
            Box(
                Modifier
                    .windowInsetsPadding(
                        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                    ).windowInsetsPadding(
                        WindowInsets.displayCutout
                    ).padding(
                        bottom = bottomSheetContentPadding
                    )
            ) {
                CurrentMapOverlayContent()
            }
        }
    }
}

val bottomSheetContentPadding: Dp
    @Composable
    get() {
        val adaptiveWindowInfo = currentWindowAdaptiveInfo()
        val bottomSheetHalfHeight = bottomSheetHalfHeight()
        val sheetValue = LocalBottomSheetState.current?.bottomSheetState?.offset
        return when (adaptiveWindowInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> min(
                viewportHeight() - (sheetValue?.px ?: 0.dp),
                viewportHeight() * bottomSheetHalfHeight
            )
            else -> 0.dp
        }
    }

val mapInsets: PaddingValues
    @Composable
    get() = WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues() +
            WindowInsets.displayCutout.only(WindowInsetsSides.End).asPaddingValues() +
            run {
                val adaptiveWindowInfo = currentWindowAdaptiveInfo()
                when (adaptiveWindowInfo.windowSizeClass.windowWidthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> PaddingValues(0.dp)
                    else -> WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues()
                }
            }