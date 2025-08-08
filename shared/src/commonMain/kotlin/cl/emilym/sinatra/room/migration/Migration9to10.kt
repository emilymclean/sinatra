package cl.emilym.sinatra.room.migration

import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

class Migration9to10: AutoMigrationSpec {

    override fun onPostMigrate(connection: SQLiteConnection) {
        super.onPostMigrate(connection)

        val ids = mutableListOf<Long>()
        connection.prepare("SELECT id, extra FROM favouriteEntity WHERE `extra` IS NULL ORDER BY `order` ASC, `id` DESC").use {
            while (it.step()) {
                ids += it.getLong(0)
            }
        }

        connection.execSQL("UPDATE favouriteEntity SET `order` = -1")
        ids.forEachIndexed { idx, id ->
            connection.prepare("UPDATE favouriteEntity SET `order` = ? WHERE `id` = ?").use {
                it.bindLong(2, id)
                it.bindInt(1, idx)
                it.step()
            }
        }
    }
}