package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.presentation.screens.maps.search.browse.QuickNavigationCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.currentLocation

@Composable
fun QuickFavouriteItemComponentContent(
    component: QuickFavouriteItemComponent
) {
    val currentLocation = currentLocation()
    LaunchedEffect(currentLocation) {
        component.updateLocation(currentLocation ?: return@LaunchedEffect)
    }

    val items by component.items.collectAsStateWithLifecycle()
    when (items.size) {
        0 -> Box(Modifier.height(1.px))
        1 -> Box(Modifier.padding(horizontal = 1.rdp)) {
            val item = remember(items) { items.first() }
            QuickNavigationCard(
                item,
                onClick = {
                    component.onClick(item)
                },
                Modifier.fillMaxWidth()
            )
        }
        else -> LazyRow(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 1.rdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.rdp, Alignment.CenterHorizontally)
        ) {
            items(
                items,
                { it.key }
            ) {
                QuickNavigationCard(
                    it,
                    onClick = { component.onClick(it) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}