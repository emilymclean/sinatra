package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

data class NavigationItem(
    val selected: (Screen) -> Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val label: @Composable (() -> Unit)? = null
) {

    @Composable
    fun RowScope.bar(
        currentScreen: Screen,
        modifier: Modifier = Modifier
    ) {
        val isSelected = remember(currentScreen) { selected(currentScreen) }
        NavigationBarItem(
            isSelected, onClick, icon, modifier, label = label
        )
    }

    @Composable
    fun ColumnScope.rail(
        currentScreen: Screen,
        modifier: Modifier = Modifier
    ) {
        val isSelected = remember(currentScreen) { selected(currentScreen) }
        NavigationRailItem(
            isSelected, onClick, icon, modifier, label = label
        )
    }

}