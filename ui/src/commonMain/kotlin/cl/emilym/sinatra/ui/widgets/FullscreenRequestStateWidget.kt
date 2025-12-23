package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget

@Composable
fun <T> FullscreenRequestStateWidget(
    state: RequestState<T>,
    retry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    when (state) {
        is RequestState.Success -> content(state.value)
        else -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                RequestStateWidget(
                    state,
                    retry
                ) {}
            }
        }
    }
}