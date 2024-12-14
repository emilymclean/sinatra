package cl.emilym.sinatra.ui

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.manualModule
import cl.emilym.sinatra.room.databaseBuilderModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

@OptIn(ExperimentalForeignApi::class)
fun init(
    versionName: String,
    versionCode: String
) {
    Napier.base(DebugAntilog())

    startKoin {
        modules(
            module {
                single { VersionInformation(versionName, versionCode) }
            },
            SharedModule().module,
            UIModule().module,
            databaseBuilderModule,
            manualModule,
        )
    }
}