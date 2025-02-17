package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cl.emilym.sinatra.data.repository.LocaleRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.ui.presentation.screens.RootMapScreen
import cl.emilym.sinatra.ui.presentation.theme.SinatraTheme
import cl.emilym.sinatra.ui.localization.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.widgets.LocalPermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueue
import cl.emilym.sinatra.ui.widgets.PermissionRequestQueueHandler
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.koin.compose.KoinContext
import org.koin.core.annotation.Factory

@Factory
class AppViewModel(
    private val transportMetadataRepository: TransportMetadataRepository,
    private val localeRepository: LocaleRepository
): ScreenModel {

    val scheduleTimeZone = MutableStateFlow(TimeZone.currentSystemDefault())

    init {
        screenModelScope.launch {
            scheduleTimeZone.value = transportMetadataRepository.timeZone()
        }
    }

    fun setLocale(locale: Locale) {
        localeRepository.languageCode = locale.toLanguageTag()
    }

}

@Composable
fun App() {
    KoinContext {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }

        Navigator(AppScreen())
    }
}

class AppScreen: Screen {
    override val key: ScreenKey = "app"

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AppViewModel>()
        val permissionQueue = remember { PermissionRequestQueue() }
        val timeZone by viewModel.scheduleTimeZone.collectAsStateWithLifecycle()

        val currentLocale = Locale.current
        LaunchedEffect(currentLocale) {
            viewModel.setLocale(currentLocale)
        }

        SinatraTheme {
            CompositionLocalProvider(
                LocalScheduleTimeZone provides timeZone,
                LocalPermissionRequestQueue provides permissionQueue
            ) {
                PermissionRequestQueueHandler()
                Navigator(RootMapScreen())
            }
        }
    }
}