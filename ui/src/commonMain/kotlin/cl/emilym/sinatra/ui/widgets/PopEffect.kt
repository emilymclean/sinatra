package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.ScreenKey
import kotlinx.coroutines.flow.Flow

val LocalPopEvent = staticCompositionLocalOf<Flow<ScreenKey>> { error("No local pop event!") }

@Composable
fun OnPopEffect(
    screenKey: ScreenKey,
    onPop: () -> Unit
) {
    val popEvent by LocalPopEvent.current.collectAsState(null)
    LaunchedEffect(popEvent) {
        if (screenKey == popEvent) {
            onPop()
        }
    }
}