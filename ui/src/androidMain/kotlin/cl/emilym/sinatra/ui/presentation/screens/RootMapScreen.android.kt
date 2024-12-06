package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.ui.canberra
import cl.emilym.sinatra.ui.maps.LineMapObject
import cl.emilym.sinatra.ui.maps.LocalMapsManager
import cl.emilym.sinatra.ui.maps.MapsManager
import cl.emilym.sinatra.ui.maps.MapsManagerSaver
import cl.emilym.sinatra.ui.maps.MarkerMapObject
import cl.emilym.sinatra.ui.maps.PolygonMapObject
import cl.emilym.sinatra.ui.presentation.screens.maps.MapSearchScreen
import cl.emilym.sinatra.ui.toMaps
import cl.emilym.sinatra.ui.widgets.LocalPopEvent
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

actual class RootMapScreen : Screen {

    @Composable
    override fun Content() {
        val mapsManager = rememberSaveable(saver = MapsManagerSaver()) { MapsManager.create() }
        val popEffectStore = remember { MutableSharedFlow<ScreenKey>() }

        val objects by mapsManager.objects.collectAsState(listOf())
        LaunchedEffect(objects) {
            Napier.d("Map object count = ${objects.size} (manager = ${mapsManager})")
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(canberra.toMaps(), 10f)
        }

        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                googleMapOptionsFactory = {
                    GoogleMapOptions()
                },
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false
                )
            ) {
                for (obj in objects) {
                    when(obj) {
                        is MarkerMapObject -> Marker(
                            state = rememberMarkerState(position = obj.position.toMaps())
                        )
                        is LineMapObject -> Polyline(obj.shape.map { it.toMaps() })
                        is PolygonMapObject -> Polygon(obj.shape.map { it.toMaps() })
                        else -> Napier.e("Unknown type ${obj::class}")
                    }
                }
            }

            val coroutineScope = rememberCoroutineScope()
            CompositionLocalProvider(
                LocalMapsManager provides mapsManager,
                LocalPopEvent provides popEffectStore
            ) {
                Navigator(
                    MapSearchScreen(),
                    onBackPressed = {
                        coroutineScope.launch {
                            popEffectStore.emit(it.key)
                        }
                        true
                    }
                )
            }
        }
    }
}