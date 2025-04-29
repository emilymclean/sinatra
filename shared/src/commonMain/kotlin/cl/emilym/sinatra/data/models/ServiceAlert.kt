package cl.emilym.sinatra.data.models

import kotlinx.datetime.Instant
import kotlin.time.Duration

enum class ServiceAlertRegion {
    BELCONNEN,
    CENTRAL_CANBERRA,
    GUNGAHLIN,
    TUGGERANONG,
    WODEN_WESTON_CREEK_MOLONGLO,
    OTHER;

    companion object {

        fun fromPB(pb: cl.emilym.gtfs.ServiceAlertRegion): ServiceAlertRegion {
            return when (pb) {
                is cl.emilym.gtfs.ServiceAlertRegion.BELCONNEN -> BELCONNEN
                is cl.emilym.gtfs.ServiceAlertRegion.CENTRAL_CANBERRA -> CENTRAL_CANBERRA
                is cl.emilym.gtfs.ServiceAlertRegion.GUNGAHLIN -> GUNGAHLIN
                is cl.emilym.gtfs.ServiceAlertRegion.TUGGERANONG -> TUGGERANONG
                is cl.emilym.gtfs.ServiceAlertRegion.WODEN_WESTON_CREEK_MOLONGLO -> WODEN_WESTON_CREEK_MOLONGLO
                is cl.emilym.gtfs.ServiceAlertRegion.UNRECOGNIZED -> OTHER
            }
        }

    }
}

data class ServiceAlert(
    val id: String,
    val title: String,
    val url: String?,
    val date: Instant?,
    val regions: List<ServiceAlertRegion>,
    val highlightDuration: Duration?,
    val viewed: Boolean = false
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.ServiceAlert): ServiceAlert {
            return ServiceAlert(
                pb.id,
                pb.title,
                pb.url,
                pb.date?.let { Instant.parse(it) },
                pb.regions.map { ServiceAlertRegion.fromPB(it) },
                pb.highlightDuration?.let { Duration.parse(it) }
            )
        }
    }

}
