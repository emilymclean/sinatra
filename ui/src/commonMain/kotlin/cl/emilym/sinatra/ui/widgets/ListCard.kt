package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import cl.emilym.compose.units.rdp
import org.jetbrains.compose.resources.painterResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.forward

@Composable
fun RandleScaffold(
    content: @Composable () -> Unit
) {
    Box(
        Modifier.size(routeRandleSize),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun ListCard(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .heightIn(min = 4.rdp)
            .then(
                when {
                    onClick != null -> Modifier.clickable {
                        onClick()
                    }
                    else -> Modifier
                }
            )
            .padding(horizontal = 1.rdp, vertical = 0.5.rdp)
            .semantics(true) {
                if (onClick != null) {
                    role = Role.Button
                }
            }
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(1.rdp)
    ) {
        Box(Modifier.clearAndSetSemantics {  }) {
            icon()
        }
        Column(Modifier.weight(1f)) {
            content()
        }
        Box(Modifier.clearAndSetSemantics {  }) {
            if (onClick != null) {
                ForwardIcon(
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}