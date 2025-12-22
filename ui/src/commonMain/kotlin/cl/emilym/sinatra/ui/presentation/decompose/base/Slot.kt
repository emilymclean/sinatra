package cl.emilym.sinatra.ui.presentation.decompose.base

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.slot.ChildSlot

@Composable
fun <C : Any, T : Any> Slot(
    slot: ChildSlot<C, T>,
    content: @Composable (child: Child.Created<C, T>) -> Unit,
) {
    content(slot.child ?: return)
}