package cl.emilym.sinatra.data.models

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

data class Alert(
    val title: String,
    val message: String?,
    val severity: AlertSeverity,
    val more: ContentLink?
)