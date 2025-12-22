package cl.emilym.sinatra.ui.presentation.decompose.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import cl.emilym.sinatra.ui.localization.LocalScheduleTimeZone
import cl.emilym.sinatra.ui.presentation.decompose.base.Slot
import cl.emilym.sinatra.ui.presentation.decompose.main.MainComponentContent
import cl.emilym.sinatra.ui.presentation.decompose.outofdate.AppOutOfDateContent
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle

@Composable
fun RootComponentContent(
    rootComponent: RootComponent
) {
    val parameters by rootComponent.localParameters.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalScheduleTimeZone provides parameters.scheduleTimeZone
    ) {
        val currentSlot by rootComponent.content.collectAsStateWithLifecycle()
        Slot(currentSlot) {
            when (val child = it.instance) {
                is RootComponent.Child.AppOutOfDateChild -> AppOutOfDateContent(child.component)
                is RootComponent.Child.Main -> MainComponentContent(child.component)
            }
        }
    }
}