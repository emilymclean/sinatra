package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.repository.StatefulPreferencesUnit
import cl.emilym.sinatra.lib.FloatRange
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesFloatSlider(
    unit: StatefulPreferencesUnit<Float>,
    range: FloatRange,
    modifier: Modifier = Modifier,
    valueDisplay: (@Composable (Float) -> Unit)? = null
) {
    val progress by unit.flow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Row(
        Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var trackProgress by remember(progress) { mutableFloatStateOf((progress - range.start) / (range.endInclusive - range.start)) }
        Box(Modifier.weight(1f)) {
            // This mess makes sure the track correctly follows the media when not
            // interacted with, follows the thumb when interacted with, and doesn't
            // jump while the backend registers that the media has been seeked
            val contentProgress = (progress - range.start) / (range.endInclusive - range.start)
            var contentProgressOnDragStart by remember { mutableStateOf(0f) }
            var isDragging by remember { mutableStateOf(false) }
            var isUpToDate by remember { mutableStateOf(true) }
            val interactionSource = remember { MutableInteractionSource() }
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is DragInteraction.Start -> {
                            isDragging = true
                            isUpToDate = false
                            contentProgressOnDragStart = contentProgress
                            trackProgress = contentProgress
                        }
                    }
                }
            }
            LaunchedEffect(trackProgress, contentProgress, isUpToDate, isDragging) {
                if (isDragging || isUpToDate) return@LaunchedEffect
                isUpToDate = if (trackProgress < contentProgressOnDragStart)
                    contentProgress <= trackProgress
                else
                    contentProgress >= trackProgress
            }

            val progress = if (!isUpToDate) {
                trackProgress
            } else {
                contentProgress
            }

            val colors = SliderDefaults.colors(
                // Always keep thumb white, it's impossible to see otherwise
                thumbColor = Color.White,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
            Slider(
                value = progress,
                onValueChange = {
                    trackProgress = it
                },
                onValueChangeFinished = {
                    scope.launch { unit.save(((range.endInclusive - range.start) * trackProgress) + range.start) }
                    isDragging = false
                },
                thumb = { sliderState ->
                    Surface(
                        shadowElevation = 3.dp,
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(colors.thumbColor)
                        )
                    }
                },
                track = { sliderState ->
                    Row(
                        Modifier.clip(RoundedCornerShape(4.dp/2))
                    ) {
                        Box(
                            Modifier.height(4.dp)
                                .fillMaxWidth(sliderState.value)
                                .background(colors.activeTrackColor)
                        )
                        Box(
                            Modifier.height(4.dp)
                                .fillMaxWidth(1f)
                                .background(colors.inactiveTrackColor)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = colors,
                interactionSource = interactionSource
            )
        }

        valueDisplay?.let {
            Box {
                it(((range.endInclusive - range.start) * trackProgress) + range.start)
            }
        }
    }
}