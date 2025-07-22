package cl.emilym.sinatra.room.migration

import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

class Migration9to10: AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        super.onPostMigrate(connection)
        connection.execSQL("UPDATE `ShaEntity` SET `lastAccessed` = `added`")
    }
}