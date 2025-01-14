package cl.emilym.sinatra.data.models

import com.google.transit.realtime.TranslatedString

enum class AlertSeverity {
    INFO, WARNING, SEVERE;

    companion object {

        fun fromPB(pb: com.google.transit.realtime.Alert.SeverityLevel?): AlertSeverity {
            return when (pb) {
                is com.google.transit.realtime.Alert.SeverityLevel.SEVERE -> SEVERE
                is com.google.transit.realtime.Alert.SeverityLevel.WARNING -> WARNING
                else -> INFO
            }
        }

    }
}

sealed interface Alert {
    val severity: AlertSeverity

    data class Content(
        val title: String,
        val message: String?,
        override val severity: AlertSeverity,
        val more: ContentLink?
    ): Alert

    data class Realtime(
        val effect: com.google.transit.realtime.Alert.Effect?,
        val cause: com.google.transit.realtime.Alert.Cause?,
        val headerText: LocalizableString?,
        val url: LocalizableString?,
        override val severity: AlertSeverity,
    ): Alert

    companion object {
        fun fromContentPB(pb: cl.emilym.gtfs.content.Banner): Alert {
            return Alert.Content(
                pb.title,
                pb.message,
                when (pb.severity) {
                    "warning" -> AlertSeverity.WARNING
                    "severe" -> AlertSeverity.SEVERE
                    else -> AlertSeverity.INFO
                },
                pbToContentLink(
                    pb.contentLinks,
                    pb.externalLinks,
                    pb.nativeLinks
                ).firstOrNull()
            )
        }
    }
}