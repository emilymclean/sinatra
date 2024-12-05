package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.TimetableServiceException
import cl.emilym.sinatra.data.models.TimetableServiceExceptionType
import cl.emilym.sinatra.data.models.TimetableServiceRegular
import kotlinx.datetime.Instant

@Entity
class TimetableServiceRegularEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val serviceId: String,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val startDate: Long,
    val endDate: Long
) {

    fun toModel(): TimetableServiceRegular {
        return TimetableServiceRegular(
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday,
            Instant.fromEpochMilliseconds(startDate),
            Instant.fromEpochMilliseconds(endDate),
        )
    }

    companion object {
        fun fromModel(entity: TimetableServiceRegular, serviceId: ServiceId): TimetableServiceRegularEntity {
            return TimetableServiceRegularEntity(
                0,
                serviceId,
                entity.monday,
                entity.tuesday,
                entity.wednesday,
                entity.thursday,
                entity.friday,
                entity.saturday,
                entity.sunday,
                entity.startDate.toEpochMilliseconds(),
                entity.endDate.toEpochMilliseconds(),
            )
        }
    }

}

@Entity
class TimetableServiceExceptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val serviceId: String,
    val date: Long,
    val type: String
) {

    fun toModel(): TimetableServiceException {
        return TimetableServiceException(
            Instant.fromEpochMilliseconds(date),
            TimetableServiceExceptionType.valueOf(type)
        )
    }

    companion object {

        fun fromModel(m: TimetableServiceException, serviceId: ServiceId): TimetableServiceExceptionEntity {
            return TimetableServiceExceptionEntity(
                0,
                serviceId,
                m.date.toEpochMilliseconds(),
                m.type.name
            )
        }

    }

}