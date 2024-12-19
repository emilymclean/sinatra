package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class NavigationItem(
    val index: Int,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val label: @Composable (() -> Unit)? = null
) {

    @Composable
    fun RowScope.bar(
        selectedIndex: Int,
        selectedCallback: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        NavigationBarItem(
            selectedIndex == index,
            {
                selectedCallback(index)
                onClick()
            },
            icon,
            modifier,
            label = label
        )
    }

    @Composable
    fun ColumnScope.rail(
        selectedIndex: Int,
        selectedCallback: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        NavigationRailItem(
            selectedIndex == index,
            {
                selectedCallback(index)
                onClick()
            },
            icon,
            modifier,
            label = label
        )
    }

}