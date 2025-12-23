package cl.emilym.sinatra.ui.presentation.decompose.root

import androidx.compose.ui.text.intl.Locale
import cl.emilym.sinatra.data.repository.LocaleRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.domain.CacheInvalidationUseCase
import cl.emilym.sinatra.domain.IsAboveMinimumVersionUseCase
import cl.emilym.sinatra.domain.migration.CompleteAppMigrationUseCase
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.base.childSlotFlow
import cl.emilym.sinatra.ui.presentation.decompose.main.DefaultMainComponent
import cl.emilym.sinatra.ui.presentation.decompose.main.MainComponent
import cl.emilym.sinatra.ui.presentation.decompose.outofdate.AppOutOfDateComponent
import cl.emilym.sinatra.ui.presentation.decompose.outofdate.DefaultAppOutOfDateComponent
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.navigate
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

data class LocalParameters(
    val scheduleTimeZone: TimeZone
)

interface RootComponent: SinatraComponent {

    val content: StateFlow<ChildSlot<*, Child>>
    val localParameters: StateFlow<LocalParameters>

    sealed interface Child {
        class AppOutOfDateChild(val component: AppOutOfDateComponent): Child
        class Main(val component: MainComponent): Child
    }

}

class DefaultRootComponent(
    sinatraComponentContext: SinatraComponentContext
): RootComponent, SinatraComponentContext by sinatraComponentContext {

    @Serializable
    private sealed interface Config {
        @Serializable
        data object AppOutOfDate : Config
        @Serializable
        data object Main : Config
    }

    private val completeAppMigrationUseCase = sinatraComponentContext.koin.get<CompleteAppMigrationUseCase>()
    private val remoteConfigRepository = sinatraComponentContext.koin.get<RemoteConfigRepository>()
    private val isAboveMinimumVersionUseCase = sinatraComponentContext.koin.get<IsAboveMinimumVersionUseCase>()
    private val localeRepository = sinatraComponentContext.koin.get<LocaleRepository>()
    private val cacheInvalidationUseCase = sinatraComponentContext.koin.get<CacheInvalidationUseCase>()
    private val transportMetadataRepository = sinatraComponentContext.koin.get<TransportMetadataRepository>()

    private val navigation = SlotNavigation<Config>()

    override val content: StateFlow<ChildSlot<*, RootComponent.Child>> =
        childSlotFlow(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = { Config.Main },
            handleBackButton = false,
            childFactory = ::createChild,
        )

    override val localParameters: StateFlow<LocalParameters> = flow {
        emit(transportMetadataRepository.timeZone())
    }.mapLatest { scheduleTimeZone ->
        LocalParameters(
            scheduleTimeZone
        )
    }.state(LocalParameters(
        TimeZone.currentSystemDefault()
    ))

    private fun createChild(config: Config, componentContext: SinatraComponentContext): RootComponent.Child =
        when (config) {
            is Config.AppOutOfDate -> RootComponent.Child.AppOutOfDateChild(
                DefaultAppOutOfDateComponent(
                    componentContext
                )
            )
            is Config.Main -> RootComponent.Child.Main(
                DefaultMainComponent(
                    componentContext
                )
            )
        }

    init {
        lifecycle.doOnCreate {
            componentScope.launch {
                completeAppMigrationUseCase()
                remoteConfigRepository.load()

                if (isAboveMinimumVersionUseCase()) return@launch
                navigation.navigate { Config.AppOutOfDate }
            }

            componentScope.launch {
                cacheInvalidationUseCase()
            }
        }

        lifecycle.doOnResume {
            localeRepository.languageCode = Locale.current.toLanguageTag()
        }
    }

}