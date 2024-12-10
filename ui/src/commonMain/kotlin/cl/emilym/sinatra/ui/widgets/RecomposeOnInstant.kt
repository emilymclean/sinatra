package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Composable
fun RecomposeOnInstants(instants: List<Instant>, content: @Composable () -> Unit) {
    val sortedInstants by remember(instants) { derivedStateOf { instants.distinct().sorted() } }
    var trigger by remember { mutableStateOf(0) }
    val clock = LocalClock.current

    LaunchedEffect(sortedInstants) {
        for (instant in sortedInstants) {
            val now = clock.now()
            val delayDuration = (instant - now)
            if (delayDuration > Duration.ZERO) {
                delay(delayDuration)
                trigger++
            }
        }
    }
    
    key(trigger) {
        content()
    }
}