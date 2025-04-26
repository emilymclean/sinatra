package cl.emilym.sinatra.ui.presentation.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlags
import cl.emilym.sinatra.data.models.Place
import cl.emilym.sinatra.data.models.RecentVisit
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.domain.search.SearchResult
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.BaseSearchWidget
import cl.emilym.sinatra.ui.widgets.IosBackButton
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.NoResultsIcon
import cl.emilym.sinatra.ui.widgets.PlaceCard
import cl.emilym.sinatra.ui.widgets.RouteCard
import cl.emilym.sinatra.ui.widgets.SearchIcon
import cl.emilym.sinatra.ui.widgets.SinatraBackHandler
import cl.emilym.sinatra.ui.widgets.SinatraTextField
import cl.emilym.sinatra.ui.widgets.StopCard
import cl.emilym.sinatra.ui.widgets.Subheading
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.imePadding
import cl.emilym.sinatra.ui.widgets.viewportHeight
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.map_search_nearby_stops
import sinatra.ui.generated.resources.no_search_results
import sinatra.ui.generated.resources.search_hint
import sinatra.ui.generated.resources.search_recently_viewed
import sinatra.ui.generated.resources.stop_detail_distance

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    onBackPressed: () -> Unit,
    onStopPressed: (Stop) -> Unit,
    onRoutePressed: (Route) -> Unit,
    onPlacePressed: (Place) -> Unit,
    extraPlaceholderContent: LazyListScope.() -> Unit = {}
) {
    val results by viewModel.results.collectAsStateWithLifecycle()

    val recentlyViewed by viewModel.recentVisits.collectAsStateWithLifecycle()
    val nearbyStops by viewModel.nearbyStops.collectAsStateWithLifecycle()

    val bottomSheetState = LocalBottomSheetState.current?.bottomSheetState
    LaunchedEffect(Unit) {
        bottomSheetState?.expand()
    }

    SinatraBackHandler(true) {
        onBackPressed()
    }

    val query by viewModel.query.collectAsStateWithLifecycle()
    Scaffold { innerPadding ->
        LazyColumn(
            Modifier.fillMaxWidth().heightIn(min = viewportHeight()),
            contentPadding = innerPadding
        ) {
            BaseSearchWidget(
                query,
                results,
                nearbyStops,
                recentlyViewed,
                onSearch = { viewModel.search(it) },
                onBackPressed,
                onStopPressed,
                onRoutePressed,
                onPlacePressed,
                extraPlaceholderContent
            )
        }
    }
}