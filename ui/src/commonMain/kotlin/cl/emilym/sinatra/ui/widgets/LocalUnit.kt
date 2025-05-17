package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.PreferencesUnit
import org.koin.compose.getKoin
import org.koin.core.qualifier.StringQualifier

@Composable
fun isMetricUnits(): Boolean {
    val koin = getKoin()
    val metricPref = remember(koin) {
        koin.get<PreferencesUnit<Boolean>>(StringQualifier(PreferencesRepository.DISPLAY_METRIC_UNITS_QUALIFIER))
    }

    return metricPref.flow.collectAsState(true).value
}

@Composable
fun override24HTimeSetting(): Time24HSetting {
    val koin = getKoin()
    val timePref = remember(koin) {
        koin.get<PreferencesUnit<Time24HSetting>>(StringQualifier(PreferencesRepository.TIME_24H_QUALIFIER))
    }

    return timePref.flow.collectAsState(Time24HSetting.AUTOMATIC).value
}