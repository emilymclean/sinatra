package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.ui.minimumTouchTarget

@Composable
fun SinatraIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(minimumTouchTarget)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
fun FavouriteButton(
    favourite: Boolean,
    onFavouriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SinatraIconButton(
        onClick = {
            onFavouriteChange(!favourite)
        },
        modifier = modifier
    ) {
        FavouriteIcon(
            favourite
        )
    }
}