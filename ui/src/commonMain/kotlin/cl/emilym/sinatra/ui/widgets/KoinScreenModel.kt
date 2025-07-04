// https://github.com/adrielcafe/voyager/issues/515
package cafe.adriel.voyager.koin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.sinatra.ui.savedstate.ScreenModelStateParametersHolder
import cl.emilym.sinatra.ui.savedstate.rememberScreenModelState
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@Composable
inline fun <reified T : ScreenModel> Screen.koinScreenModel(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null
): T {
    val screenModelState = rememberScreenModelState()
    val holder = remember(
        screenModelState,
        parameters
    ) {
        ScreenModelStateParametersHolder(
            parameters,
            screenModelState
        )
    }
//    val currentParameters by rememberUpdatedState(parameters)
    val tag = remember(qualifier, scope) { qualifier?.value }
    return rememberScreenModel(tag = tag) {
        scope.get(qualifier) {
            holder
        }
    }
}