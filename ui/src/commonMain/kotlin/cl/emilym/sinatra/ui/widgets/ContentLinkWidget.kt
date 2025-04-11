package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.models.NativePageReference
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.ui.minimumTouchTarget
import cl.emilym.sinatra.ui.presentation.screens.AboutScreen
import cl.emilym.sinatra.ui.presentation.screens.ContentScreen
import cl.emilym.sinatra.ui.presentation.screens.ServiceAlertScreen
import cl.emilym.sinatra.ui.presentation.theme.Container

fun contentRoute(id: ContentId): Screen {
    return when (id) {
        ContentRepository.ABOUT_ID -> AboutScreen()
        else -> ContentScreen(id)
    }
}

fun nativeRoute(reference: NativePageReference): Screen? {
    return when (reference) {
        "service-updates" -> ServiceAlertScreen()
        else -> null
    }
}

@Composable
fun ContentLinkWidget(
    link: ContentLink,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier
            .heightIn(min = minimumTouchTarget)
            .background(Container)
            .semantics(mergeDescendants = true) {
                role = Role.Button
            }
            .clickable {
                when (link) {
                    is ContentLink.External -> {
                        uriHandler.openUri(link.url)
                    }
                    is ContentLink.Content -> {
                        navigator.push(contentRoute(link.id))
                    }
                    is ContentLink.Native -> {
                        nativeRoute(link.nativeReference)?.let {
                            navigator.push(it)
                        }
                    }
                    else -> {}
                }
            }
            .padding(horizontal = 1.rdp, vertical = 0.75.rdp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
    ) {
        Text(link.title, modifier = Modifier.weight(1f))
        Box(Modifier.clearAndSetSemantics {  }) {
            when (link) {
                is ContentLink.External -> {
                    ExternalLinkIcon(
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                else -> {
                    ForwardIcon(
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}