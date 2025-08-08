package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat

@Composable
actual fun rememberHapticFeedback(): SinatraHapticFeedback {
    val view = LocalView.current
    return remember(view) {
        object : SinatraHapticFeedback {
            override fun perform(feedbackType: SinatraHapticFeedbackType) {
                ViewCompat.performHapticFeedback(
                    view,
                    when (feedbackType) {
                        SinatraHapticFeedbackType.FREQUENT_TICK -> HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
                        SinatraHapticFeedbackType.GESTURE_START -> HapticFeedbackConstantsCompat.GESTURE_START
                        SinatraHapticFeedbackType.GESTURE_END -> HapticFeedbackConstantsCompat.GESTURE_END
                        SinatraHapticFeedbackType.LONG_PRESS -> HapticFeedbackConstantsCompat.LONG_PRESS
                    }
                )
            }
        }
    }
}