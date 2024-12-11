package cl.emilym.sinatra.android

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.ui.UIModule
import cl.emilym.sinatra.ui.VersionInformation
import org.koin.core.annotation.Module
import org.koin.dsl.module
import cl.emilym.sinatra.android.BuildConfig

@Module(includes = [SharedModule::class, UIModule::class])
class AppModule

val versionInformationModule = module {
    single {
        VersionInformation(
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
    }
}