package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.presentation.screens.AddSpecialFavouriteScreen
import cl.emilym.sinatra.ui.presentation.screens.Icon
import cl.emilym.sinatra.ui.presentation.screens.label
import cl.emilym.sinatra.ui.presentation.screens.maps.navigate.NavigateEntryScreen
import cl.emilym.sinatra.ui.widgets.QuickSelectCard
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuickNavigateGroupBrowseOption(option: BrowsePrompt.QuickNavigateGroup) {
    when (option.items.size) {
        1 -> Box(Modifier.padding(horizontal = 1.rdp)) {
            QuickNavigationCard(option.items.first(), Modifier.fillMaxWidth())
        }
        else -> LazyRow(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 1.rdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.rdp, Alignment.CenterHorizontally)
        ) {
            items(
                option.items,
                { it.key }
            ) {
                QuickNavigationCard(
                    it,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun QuickNavigationCard(
    item: QuickNavigationItem,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.currentOrThrow
    QuickSelectCard(
        {
            when (item) {
                is QuickNavigationItem.Item -> navigator.push(NavigateEntryScreen(item.location))
                is QuickNavigationItem.ToAdd -> navigator.push(AddSpecialFavouriteScreen(item.special))
            }
        },
        null,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.then(modifier)
    ) {
        item.special?.Icon() ?: (item as? QuickNavigationItem.Item)?.let {
            Icon(painterResource(it.location.icon), contentDescription = null)
        }
        Text(when (item) {
            is QuickNavigationItem.Item -> item.location.name
            is QuickNavigationItem.ToAdd ->  item.special.label
        })
    }
}