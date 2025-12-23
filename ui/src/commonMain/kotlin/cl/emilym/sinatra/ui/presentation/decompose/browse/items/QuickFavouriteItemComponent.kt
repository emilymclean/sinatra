package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.domain.prompt.QuickNavigateUseCase
import cl.emilym.sinatra.domain.prompt.SpecialAddUseCase
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigationLocation
import cl.emilym.sinatra.ui.presentation.screens.maps.search.browse.QuickNavigationItem
import cl.emilym.sinatra.ui.toNavigationLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

interface QuickFavouriteItemComponent: BrowseItemPlugin<List<QuickNavigationItem>> {

    fun onClick(item: QuickNavigationItem)
    fun updateLocation(location: MapLocation)

}

class DefaultQuickFavouriteItemComponent(
    private val onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): QuickFavouriteItemComponent, SinatraComponentContext by sinatraComponentContext {

    private val quickNavigateUseCase: QuickNavigateUseCase = koin.get()
    private val specialAddUseCase: SpecialAddUseCase = koin.get()

    private val currentLocation = MutableStateFlow<MapLocation?>(null)

    override val state =
        combine(
            currentLocation.flatRequestStateFlow(showLoading = false) {
                withContext(Dispatchers.IO) {
                    quickNavigateUseCase(it)
                        .mapLatest {
                            it.mapNotNull {
                                QuickNavigationItem.Item(
                                    it.navigation.toNavigationLocation() ?: return@mapNotNull null,
                                    it.specialType
                                )
                            }
                        }
                }
            },
            flatRequestStateFlow {
                withContext(Dispatchers.IO) {
                    specialAddUseCase().mapLatest { it.map { QuickNavigationItem.ToAdd(it) } }
                }
            }
        ) { quickNavigation, specialAdd ->
            (quickNavigation.unwrap().nullIfEmpty() ?: listOf()) + (specialAdd.unwrap().nullIfEmpty() ?: listOf())
        }
            .mapLatest {
                when (it.isEmpty()) {
                    true -> BrowseItemContent.None()
                    else -> BrowseItemContent.Content(it)
                }
            }
            .state(BrowseItemContent.None())

    override fun onClick(item: QuickNavigationItem) {
        onNavigate(
            when (item) {
                is QuickNavigationItem.Item -> NavigationInstruction.NavigationEntry(
                    destination = item.location
                )
                is QuickNavigationItem.ToAdd -> NavigationInstruction.SetSpecialFavourite(
                    item.special
                )
            }
        )
    }

    override fun updateLocation(location: MapLocation) {
        currentLocation.value = location
    }

}