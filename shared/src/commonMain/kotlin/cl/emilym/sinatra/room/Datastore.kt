package cl.emilym.sinatra.room

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val PREFERENCES_DATASTORE_NAME = "preferences.preferences_pb"
const val PREFERENCES_DATASTORE_QUALIFIER = "preferences"

internal const val APP_DATASTORE_NAME = "app.preferences_pb"
const val APP_DATASTORE_QUALIFIER = "app"

internal fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )