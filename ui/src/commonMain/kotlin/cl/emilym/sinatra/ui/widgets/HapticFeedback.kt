package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable

enum class SinatraHapticFeedbackType {
    FREQUENT_TICK, GESTURE_START, GESTURE_END, LONG_PRESS
}

interface SinatraHapticFeedback {
    fun perform(feedbackType: SinatraHapticFeedbackType)
}

@Composable
expect fun rememberHapticFeedback(): SinatraHapticFeedback