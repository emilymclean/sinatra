package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cl.emilym.compose.units.rdp

@Composable
fun ListHint(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 3.rdp).then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Box(Modifier.width(0.5.rdp))
        Text(text, textAlign = TextAlign.Center)
    }
}