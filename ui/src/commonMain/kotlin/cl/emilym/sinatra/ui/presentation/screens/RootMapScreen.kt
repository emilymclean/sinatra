package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.e
import cl.emilym.sinatra.ui.navigation.CurrentBottomSheetContent
import cl.emilym.sinatra.ui.navigation.CurrentMapOverlayContent
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.navigation.MapScope
import cl.emilym.sinatra.ui.navigation.bottomSheetHalfHeight
import cl.emilym.sinatra.ui.presentation.screens.maps.MapSearchScreen
import cl.emilym.sinatra.ui.widgets.LocalPermissionState
import cl.emilym.sinatra.ui.widgets.MyLocationIcon
import cl.emilym.sinatra.ui.widgets.PermissionState
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffold
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetState
import cl.emilym.sinatra.ui.widgets.bottomsheet.rememberSinatraBottomSheetScaffoldState
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.aakira.napier.Napier
import io.ktor.http.ParametersBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.KoinApplication.Companion.init
import org.koin.core.parameter.ParametersHolder

@KoinViewModel
class MapViewModel(
    private val permissionsController: PermissionsController
): ViewModel() {

    val hasLocationPermission = MutableStateFlow(false)

    init {
        requestPermissions()
    }

    fun requestPermissions() {
        viewModelScope.launch {
            hasLocationPermission.value = if (!permissionsController.isPermissionGranted(Permission.LOCATION)) {
                try {
                    permissionsController.providePermission(Permission.LOCATION)
                    true
                } catch (e: Exception) {
                    Napier.e(e)
                    false
                }
            } else {
                true
            }
        }
    }

}

@Composable
expect fun Map(overlay: @Composable MapScope.() -> Unit)

class RootMapScreen: Screen {

    @Composable
    override fun Content() {
        val permissionsFactory = rememberPermissionsControllerFactory()
        val permissionsController: PermissionsController = remember(permissionsFactory) {
            permissionsFactory.createPermissionsController()
        }
        BindEffect(permissionsController)
        val viewModel = koinViewModel<MapViewModel> { ParametersHolder(
            mutableListOf(permissionsController)
        ) }

        val hasLocationPermission by viewModel.hasLocationPermission.collectAsState(false)

        CompositionLocalProvider(
            LocalPermissionState provides PermissionState(hasLocationPermission)
        ) {
            Navigator(
                MapSearchScreen()
            ) {
                Scaffold {
                    Box(Modifier.fillMaxSize()) {
                        Map {
                            MapControls()
                        }
                        MapOverlay()
                    }
                }
            }
        }
    }

    @Composable
    private fun MapScope.MapControls() {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeContent.asPaddingValues())
        ) {
            val hasLocationPermission = LocalPermissionState.current.hasLocationPermission
            val (locationPermissionButtonRef) = createRefs()

            val padding = 1.rdp

            if (hasLocationPermission) {
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier.constrainAs(locationPermissionButtonRef) {
                        end.linkTo(parent.end, padding)
                        bottom.linkTo(parent.bottom, 56.dp)
                    }
                ) { MyLocationIcon() }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Scaffold(
        content: @Composable () -> Unit
    ) {
        val sheetState = rememberSinatraBottomSheetState(
            initialValue = SinatraSheetValue.HalfExpanded,
            skipHiddenState = true
        )
        val state = rememberSinatraBottomSheetScaffoldState(
            bottomSheetState = sheetState
        )

        SinatraBottomSheetScaffold(
            scaffoldState = state,
            sheetContent = {
                val coroutineScope = rememberCoroutineScope()
                SinatraBackHandler(state.bottomSheetState.targetValue == SinatraSheetValue.Expanded) {
                    coroutineScope.launch {
                        state.bottomSheetState.halfExpand()
                    }
                }
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