package cl.emilym.sinatra.ui.presentation.decompose.base

import com.arkivanov.decompose.GenericComponentContext
import com.arkivanov.decompose.router.children.NavigationSource
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer

fun <Ctx : GenericComponentContext<Ctx>, C : Any, T : Any> Ctx.childStackFlow(
    source: NavigationSource<StackNavigation.Event<C>>,
    serializer: KSerializer<C>?,
    initialConfiguration: C,
    key: String = "DefaultChildStack",
    handleBackButton: Boolean = false,
    childFactory: (configuration: C, Ctx) -> T
): StateFlow<ChildStack<C, T>> = childStack(
    source,
    serializer,
    initialConfiguration,
    key,
    handleBackButton,
    childFactory
).asStateFlow()

fun <Ctx : GenericComponentContext<Ctx>, C : Any, T : Any> Ctx.childSlotFlow(
    source: NavigationSource<SlotNavigation.Event<C>>,
    serializer: KSerializer<C>?,
    initialConfiguration: () -> C? = { null },
    key: String = "DefaultChildSlot",
    handleBackButton: Boolean = false,
    childFactory: (configuration: C, Ctx) -> T,
): StateFlow<ChildSlot<C, T>> = childSlot(
    source,
    serializer,
    initialConfiguration,
    key,
    handleBackButton,
    childFactory
).asStateFlow()