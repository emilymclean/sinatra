package cl.emilym.sinatra.ui.presentation.decompose.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.ui.presentation.decompose.base.Slot
import cl.emilym.sinatra.ui.presentation.decompose.main.tab.TabBarComponentContent
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.screenSize

@Composable
fun MainComponentContent(component: MainComponent) {
    val screenSize = screenSize()
    val verticalOrientation = remember(screenSize) {
        screenSize.height >= screenSize.width
    }

    if (verticalOrientation) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .consumeWindowInsets(
                        WindowInsets.safeContent.only(WindowInsetsSides.Bottom)
                    )
                    .weight(1f)
            ) {
                MainComponentContentPage(component)
            }
            Slot(component.tabBarContent.collectAsStateWithLifecycle().value) {
                when (val child = it.instance) {
                    is MainComponent.TabBarChild.Main -> TabBarComponentContent(
                        child.component,
                        true
                    )
                }
            }
        }
    } else {
        Row {
            Slot(component.tabBarContent.collectAsStateWithLifecycle().value) {
                when (val child = it.instance) {
                    is MainComponent.TabBarChild.Main -> TabBarComponentContent(
                        child.component,
                        false
                    )
                }
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .consumeWindowInsets(
                        WindowInsets.safeContent.only(WindowInsetsSides.Start)
                    )
                    .weight(1f)
            ) {
                MainComponentContentPage(component)
            }
        }
    }

}