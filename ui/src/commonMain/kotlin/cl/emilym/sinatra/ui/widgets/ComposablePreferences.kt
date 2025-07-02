package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.PreferencesUnit
import org.koin.compose.koinInject
import org.koin.core.qualifier.StringQualifier

@Composable
private fun <T> composablePreference(
    qualifier: StringQualifier,
    default: T
): T {
    val pref = koinInject<PreferencesUnit<T>>(qualifier)
    var value by remember { mutableStateOf(default) }

    LaunchedEffect(pref) {
        pref.flow.collect {
            value = it
        }
    }

    return value
}

@Composable
fun isMetricUnits(): Boolean {
    return composablePreference(
        StringQualifier(PreferencesRepository.DISPLAY_METRIC_UNITS_QUALIFIER),
        true
    )
}

@Composable
fun override24HTimeSetting(): Time24HSetting {
    return composablePreference(
        StringQualifier(PreferencesRepository.TIME_24H_QUALIFIER),
        Time24HSetting.AUTOMATIC
    )
}