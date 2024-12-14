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
import androidx.compose.ui.platform.LocalUriHandler
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.ui.minimumTouchTarget

@Composable
fun ContentLinkWidget(
    link: ContentLink,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .heightIn(min = minimumTouchTarget)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .clickable {
                when (link) {
                    is ContentLink.External -> {
                        uriHandler.openUri(link.url)
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
        }
    }
}