package cl.emilym.sinatra.ui.presentation.decompose.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cl.emilym.sinatra.ui.presentation.decompose.base.Slot
import cl.emilym.sinatra.ui.presentation.decompose.outofdate.AppOutOfDateContent
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle

@Composable
fun RootComponentContent(
    rootComponent: RootComponent
) {
    val currentSlot by rootComponent.content.collectAsStateWithLifecycle()
    Slot(currentSlot) {
        when (val child = it.instance) {
            is RootComponent.Child.AppOutOfDateChild -> AppOutOfDateContent(child.component)
            else -> {}
        }
    }
}