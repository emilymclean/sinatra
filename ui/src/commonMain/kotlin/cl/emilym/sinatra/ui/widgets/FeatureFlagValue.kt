package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

@Composable
fun FeatureFlag.value(): Boolean {
    val remoteConfigRepository = koinInject<RemoteConfigRepository>()
    if (remoteConfigRepository.loaded) return remoteConfigRepository.featureImmediate(this@value)
    var value by remember { mutableStateOf(default) }

    LaunchedEffect(name) {
        try {
            value = remoteConfigRepository.feature(this@value)
        } catch(e: Exception) {
            Napier.e(e)
        }

    }

    return value
}