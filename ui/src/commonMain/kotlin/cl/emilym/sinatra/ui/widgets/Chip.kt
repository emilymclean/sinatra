package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.repository.isIos

@Composable
fun Chip(
    selected: Boolean,
    onToggle: (selected: Boolean) -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    unselectedBackground: Color = MaterialTheme.colorScheme.surface,
    selectedBackground: Color = MaterialTheme.colorScheme.primary.copy(
        alpha = 0.25f
    ).compositeOver(MaterialTheme.colorScheme.surface),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .height(2.rdp)
            .background(
                when (selected) {
                    false -> unselectedBackground
                    true -> selectedBackground
                }
            )
            .clickable {
                onToggle(!selected)
            }
            .semantics {
                this.selected = selected
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
                this.role = Role.Checkbox
            }
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.extraSmall)
            .padding(0.5.rdp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides MaterialTheme.typography.labelSmall.fixIos()
        ) {
            content()
        }
    }
}