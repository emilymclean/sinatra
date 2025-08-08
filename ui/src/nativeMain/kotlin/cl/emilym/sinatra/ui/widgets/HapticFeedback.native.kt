package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

@Composable
actual fun rememberHapticFeedback(): SinatraHapticFeedback {
    return remember {
        object : SinatraHapticFeedback {
            override fun perform(feedbackType: SinatraHapticFeedbackType) {
                UIImpactFeedbackGenerator(
                    when (feedbackType) {
                        SinatraHapticFeedbackType.FREQUENT_TICK -> UIImpactFeedbackStyle.UIImpactFeedbackStyleSoft
                        SinatraHapticFeedbackType.GESTURE_START -> UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
                        SinatraHapticFeedbackType.GESTURE_END -> UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
                        SinatraHapticFeedbackType.LONG_PRESS -> UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
                    }
                ).impactOccurred()
            }
        }
    }
}