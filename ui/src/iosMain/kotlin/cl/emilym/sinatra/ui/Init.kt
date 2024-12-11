package cl.emilym.sinatra.ui

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.manualModule
import cl.emilym.sinatra.room.databaseBuilderModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

val copyPastedAppModuleBecauseKMPIsBroken = module {
    viewModel() { _ -> cl.emilym.sinatra.ui.AppViewModel(transportMetadataRepository=get(),cleanupUseCase=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.AboutScreenViewModel(versionInformation=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.FavouriteViewModel(favouriteRepository=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.HomeViewModel(stopRepository=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.maps.MapSearchViewModel(stopRepository=get(),routeStopSearchUseCase=get(),recentVisitRepository=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.maps.RouteDetailViewModel(currentTripForRouteUseCase=get(),favouriteRepository=get(),recentVisitRepository=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.maps.RouteListViewModel(displayRoutesUseCase=get()) }
    viewModel() { _ -> cl.emilym.sinatra.ui.presentation.screens.maps.StopDetailViewModel(stopRepository=get(),upcomingRoutesForStopUseCase=get(),favouriteRepository=get(),recentVisitRepository=get()) }
}

fun init() {
    Napier.base(DebugAntilog())

    startKoin {
        modules(
            SharedModule().module,
            copyPastedAppModuleBecauseKMPIsBroken,
            databaseBuilderModule,
            manualModule,
            module {
                single { VersionInformation("Version", 0) }
            }
        )
    }
}