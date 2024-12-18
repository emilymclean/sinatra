package cl.emilym.sinatra.ui

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.data.client.RemoteConfigWrapper
import cl.emilym.sinatra.manualModule
import cl.emilym.sinatra.room.databaseBuilderModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.module

@OptIn(ExperimentalForeignApi::class)
fun init(
    remoteConfig: RemoteConfigProtocol,
    versionName: String,
    versionCode: String
) {
    Napier.base(DebugAntilog())

    startKoin {
        modules(
            module {
                single { AppleRemoteConfigWrapper(remoteConfig) } bind RemoteConfigWrapper::class
                single {
                    BuildInformation(
                        versionName,
                        versionCode,
                        NOMINATIM_EMAIL,
                        NOMINATIM_USER_AGENT
                    )
                }
            },
            SharedModule().module,
            UIModule().module,
            databaseBuilderModule,
            manualModule,
        )
    }
}