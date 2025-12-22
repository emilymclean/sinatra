package cl.emilym.sinatra.ui.presentation.decompose.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.invisibleToUser
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.ui.maps.rememberMapControl
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.presentation.decompose.main.MainComponent.Child
import cl.emilym.sinatra.ui.presentation.screens.Map
import cl.emilym.sinatra.ui.presentation.screens.bottomSheetContentPadding
import cl.emilym.sinatra.ui.widgets.LocalMapControl
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.ViewportSizeWidget
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetScaffoldState
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.value
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainComponentContentPage(
    component: MainComponent
) {
    val mapControl = rememberMapControl()
    val content by component.content.collectAsStateWithLifecycle()
    val scaffoldState = rememberSinatraBottomSheetScaffoldState()

    ViewportSizeWidget {
        when (val child = content.active.instance) {
            is Child.PageChild<*> -> {
                Children(
                    content,
                    modifier = Modifier.fillMaxSize()
                ) {

                }
            }
            is Child.MapChild<*,*> -> {
                BottomSheet(
                    scaffoldState,
                    content,
                ) {
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

                        CompositionLocalProvider(
                            LocalMapControl provides mapControl
                        ) {
                            MapOverlayContent(child)
                        }
                    }
                }
            }
        }
    }


}

@Composable
private fun BottomSheet(
    scaffoldState: SinatraBottomSheetScaffoldState,
    stack: ChildStack<*, Child>,
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
private fun MapOverlayContent(
    child: Child.MapChild<*,*>
) {
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
            when (child) {
                else -> {}
            }
        }
    }
}