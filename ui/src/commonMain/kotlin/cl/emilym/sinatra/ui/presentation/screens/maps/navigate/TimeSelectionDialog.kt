package cl.emilym.sinatra.ui.presentation.screens.maps.navigate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.ui.localization.LocalClock
import cl.emilym.sinatra.ui.localization.LocalLocalTimeZone
import cl.emilym.sinatra.ui.widgets.value
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.time_select_button_cancel
import sinatra.ui.generated.resources.time_select_button_now
import sinatra.ui.generated.resources.time_select_button_ok
import sinatra.ui.generated.resources.time_select_tab_arrive
import sinatra.ui.generated.resources.time_select_tab_depart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionDialog(
    selectedTime: NavigationAnchorTime,
    onTimeSelected: (NavigationAnchorTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val clock = LocalClock.current
    val timeZone = LocalLocalTimeZone.current
    val initialInstant = remember(selectedTime, timeZone) {
        when (selectedTime) {
            is NavigationAnchorTime.Now -> clock.now()
            is NavigationAnchorTime.DepartureTime -> selectedTime.time
            is NavigationAnchorTime.ArrivalTime -> selectedTime.time
        }.toLocalDateTime(timeZone)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialInstant.hour,
        initialMinute = initialInstant.minute
    )

    var selectedTabIndex by remember { mutableStateOf(
        when (selectedTime) {
            is NavigationAnchorTime.Now, is NavigationAnchorTime.DepartureTime -> 0
            else -> 1
        }
    ) }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                TabRow(
                    selectedTabIndex
                ) {
                    Tab(
                        selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        modifier = Modifier.padding(0.5.rdp)
                    ) {
                        Text(stringResource(Res.string.time_select_tab_depart))
                    }
                    if (FeatureFlag.RAPTOR_ARRIVAL_BASED_ROUTING.value()) {
                        Tab(
                            selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            modifier = Modifier.padding(0.5.rdp)
                        ) {
                            Text(stringResource(Res.string.time_select_tab_arrive))
                        }
                    }
                }
                Spacer(Modifier.height(1.rdp))
                Column(
                    modifier = Modifier.padding(1.rdp),
                    verticalArrangement = Arrangement.spacedBy(1.rdp)
                ) {
                    TimePicker(
                        timePickerState,
//                        modifier = Modifier.padding(horizontal = 1.rdp)
                    )
                    if (selectedTabIndex == 0) {
                        Button(
                            onClick = {
                                onTimeSelected(NavigationAnchorTime.Now)
                                onDismissRequest()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(Res.string.time_select_button_now))
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(stringResource(Res.string.time_select_button_cancel))
                        }
                        Spacer(Modifier.height(0.5.rdp))
                        Button(
                            onClick = {
                                val currentInstant = clock.now().toLocalDateTime(timeZone)
                                if (
                                    selectedTabIndex == 0 &&
                                    timePickerState.hour == currentInstant.hour &&
                                    timePickerState.minute == currentInstant.minute
                                ) {
                                    onTimeSelected(NavigationAnchorTime.Now)
                                } else {
                                    val instant = LocalDateTime(
                                        initialInstant.year,
                                        initialInstant.monthNumber,
                                        initialInstant.dayOfMonth,
                                        timePickerState.hour,
                                        timePickerState.minute,
                                        0
                                    ).toInstant(timeZone)

                                    onTimeSelected(when(selectedTabIndex) {
                                        0 -> NavigationAnchorTime.DepartureTime(instant)
                                        else -> NavigationAnchorTime.ArrivalTime(instant)
                                    })
                                }
                                onDismissRequest()
                            },
                        ) {
                            Text(stringResource(Res.string.time_select_button_ok))
                        }
                    }
                }
            }
        }
    }
}