package cl.emilym.sinatra.room.migration

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(
    tableName = "RouteEntity",
    columnName = "realTimeUrl"
)
class Migration7to8: AutoMigrationSpec