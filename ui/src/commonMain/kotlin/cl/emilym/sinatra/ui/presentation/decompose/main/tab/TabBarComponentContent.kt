package cl.emilym.sinatra.ui.presentation.decompose.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.ui.widgets.InfoIcon
import cl.emilym.sinatra.ui.widgets.JourneyIcon
import cl.emilym.sinatra.ui.widgets.MapIcon
import cl.emilym.sinatra.ui.widgets.NavigationItem
import cl.emilym.sinatra.ui.widgets.StarOutlineIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_favourites
import sinatra.ui.generated.resources.navigation_bar_map
import sinatra.ui.generated.resources.navigation_bar_more
import sinatra.ui.generated.resources.navigation_bar_navigate

@Composable
fun TabBarComponentContent(
    component: TabBarComponent,
    verticalOrientation: Boolean
) {
    val state by component.state.collectAsStateWithLifecycle()
    val items = remember(state.availableTabBarItems) {
        state.availableTabBarItems.mapIndexed { i, it ->
            when (it) {
                TabBarItem.MAP -> NavigationItem(
                    i,
                    {
                        component.navigate(TabBarItem.MAP)
                    },
                    { MapIcon() },
                    { Text(stringResource(Res.string.navigation_bar_map)) }
                )
                TabBarItem.NAVIGATE -> NavigationItem(
                    i,
                    {
                        component.navigate(TabBarItem.NAVIGATE)
                    },
                    { JourneyIcon() },
                    { Text(stringResource(Res.string.navigation_bar_navigate)) }
                )
                TabBarItem.FAVOURITES -> NavigationItem(
                    i,
                    {
                        component.navigate(TabBarItem.FAVOURITES)
                    },
                    { StarOutlineIcon() },
                    { Text(stringResource(Res.string.navigation_bar_favourites)) }
                )
                TabBarItem.MORE -> NavigationItem(
                    i,
                    {
                        component.navigate(TabBarItem.MORE)
                    },
                    { InfoIcon() },
                    { Text(stringResource(Res.string.navigation_bar_more)) }
                )
            }
        }
    }

    when (verticalOrientation) {
        true -> NavigationBar {
            for (item in items) {
                with (item) {
                    bar(
                        state.availableTabBarItems.indexOf(state.selectedTabBarItem),
                        selectedCallback = {}
                    )
                }
            }
        }
        else -> Box(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(WindowInsets.displayCutout.only(WindowInsetsSides.Start).asPaddingValues())
        ) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                for (item in items) {
                    with (item) {
                        rail(
                            state.availableTabBarItems.indexOf(state.selectedTabBarItem),
                            selectedCallback = {}
                        )
                    }
                }
            }
        }
    }
}