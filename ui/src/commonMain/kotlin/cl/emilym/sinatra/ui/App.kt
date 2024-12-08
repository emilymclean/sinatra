package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.ui.presentation.screens.RootMapScreen
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme
import cl.emilym.sinatra.ui.widgets.LocalScheduleTimeZone
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@KoinViewModel
class AppViewModel(
    private val transportMetadataRepository: TransportMetadataRepository
): ViewModel() {

    val scheduleTimeZone = MutableStateFlow(TimeZone.currentSystemDefault())

    init {
        viewModelScope.launch {
            scheduleTimeZone.value = transportMetadataRepository.timeZone()
        }
    }

}

@Composable
fun App() {
    KoinContext {
        val viewModel = koinViewModel<AppViewModel>()
        // Configuration
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }

        val timeZone by viewModel.scheduleTimeZone.collectAsState(TimeZone.currentSystemDefault())
        SinatraTheme {
            CompositionLocalProvider(
                LocalScheduleTimeZone provides timeZone
            ) {
                Navigator(RootMapScreen())
            }
        }
    }
}