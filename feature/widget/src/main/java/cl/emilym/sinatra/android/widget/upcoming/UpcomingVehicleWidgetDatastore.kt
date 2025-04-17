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
import cl.emilym.sinatra.android.widget.data.proto.UpcomingVehicleState
import com.google.protobuf.InvalidProtocolBufferException
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object UpcomingVehicleWidgetSerializer: Serializer<UpcomingVehicleState> {

    override val defaultValue: UpcomingVehicleState = UpcomingVehicleState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UpcomingVehicleState {
        try {
            return UpcomingVehicleState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UpcomingVehicleState, output: OutputStream) = t.writeTo(output)

}

object UpcomingVehicleWidgetState: GlanceStateDefinition<UpcomingVehicleState> {

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<UpcomingVehicleState> {
        return context.upcomingVehicleWidgetDatastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.preferencesDataStoreFile(fileKey)
    }

}

val Context.upcomingVehicleWidgetDatastore: DataStore<UpcomingVehicleState> by dataStore(
    fileName = "upcoming-vehicle-widget.pb",
    serializer = UpcomingVehicleWidgetSerializer
)