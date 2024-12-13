package cl.emilym.sinatra.ui

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.manualModule
import cl.emilym.sinatra.room.databaseBuilderModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

fun init(
    versionName: String,
    versionCode: String
) {
    Napier.base(DebugAntilog())

    startKoin {
        modules(
            SharedModule().module,
            UIModule().module,
            databaseBuilderModule,
            manualModule,
            module {
                single { VersionInformation(versionName, versionCode) }
            }
        )
    }
}