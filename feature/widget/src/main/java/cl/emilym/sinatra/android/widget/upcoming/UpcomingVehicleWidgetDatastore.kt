package cl.emilym.sinatra.android.widget.upcoming

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.glance.state.GlanceStateDefinition
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleData
import com.google.protobuf.InvalidProtocolBufferException
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object UpcomingVehicleWidgetSerializer: Serializer<UpcomingVehicleData> {

    override val defaultValue: UpcomingVehicleData = UpcomingVehicleData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UpcomingVehicleData {
        try {
            return UpcomingVehicleData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UpcomingVehicleData, output: OutputStream) = t.writeTo(output)

}

object UpcomingVehicleWidgetState: GlanceStateDefinition<UpcomingVehicleData> {

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<UpcomingVehicleData> {
        return context.upcomingVehicleWidgetDatastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.preferencesDataStoreFile(fileKey)
    }

}

val Context.upcomingVehicleWidgetDatastore: DataStore<UpcomingVehicleData> by dataStore(
    fileName = "upcoming-vehicle-widget.pb",
    serializer = UpcomingVehicleWidgetSerializer
)