package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.ui.minimumTouchTarget
import cl.emilym.sinatra.ui.presentation.screens.AboutScreen
import cl.emilym.sinatra.ui.presentation.screens.ContentScreen
import cl.emilym.sinatra.ui.presentation.theme.Container

fun contentRoute(id: ContentId): Screen {
    return when (id) {
        ContentRepository.ABOUT_ID -> AboutScreen()
        else -> ContentScreen(id)
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
            .clickable {
                when (link) {
                    is ContentLink.External -> {
                        uriHandler.openUri(link.url)
                    }
                    is ContentLink.Content -> {
                        navigator.push(contentRoute(link.id))
                    }
                }
            }
            .padding(horizontal = 1.rdp, vertical = 0.75.rdp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
    ) {
        Text(link.title, modifier = Modifier.weight(1f))
        when (link) {
            is ContentLink.External -> {
                ExternalLinkIcon(
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            is ContentLink.Content -> {
                ForwardIcon(
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}