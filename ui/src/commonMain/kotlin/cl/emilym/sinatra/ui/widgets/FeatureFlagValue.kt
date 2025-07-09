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
    var value by remember(name) { mutableStateOf(when (remoteConfigRepository.loaded) {
        true -> remoteConfigRepository.featureImmediate(this@value)
        else -> default
    }) }

    LaunchedEffect(name) {
        try {
            value = when (remoteConfigRepository.loaded) {
                true -> remoteConfigRepository.featureImmediate(this@value)
                else -> remoteConfigRepository.feature(this@value)
            }
        } catch(e: Exception) {
            Napier.e(e)
        }

    }

    return value
}