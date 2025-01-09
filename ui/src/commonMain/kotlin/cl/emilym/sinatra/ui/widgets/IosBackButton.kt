package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue
import kotlinx.coroutines.launch

@Composable
fun BackButton(onBack: () -> Unit) {
    SinatraIconButton(
        onClick = { onBack() },
        modifier = Modifier.semantics {
            contentDescription = "Back"
        }
    ) {
        BackIcon()
    }
}

@Composable
fun NavigatorBackButton() {
    val navigator = LocalNavigator.currentOrThrow
    if (!navigator.canPop) return

    BackButton { navigator.pop() }
}

@Composable
expect fun IosBackButton(onBack: () -> Unit)

@Composable
fun SheetIosBackButton() {
    val navigator = LocalNavigator.current
    val sheetState = LocalBottomSheetState.current?.bottomSheetState
    val coroutineScope = rememberCoroutineScope()

    IosBackButton {
        when {
            sheetState?.currentValue == SinatraSheetValue.Expanded -> coroutineScope.launch {
                sheetState.halfExpand()
            }
            navigator?.canPop == true -> navigator.pop()
        }
    }
}