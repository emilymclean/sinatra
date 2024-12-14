package cl.emilym.sinatra.android

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.ui.UIModule
import cl.emilym.sinatra.ui.VersionInformation
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module(includes = [SharedModule::class, UIModule::class])
@ComponentScan
class AppModule

val nativeModule = module {
    single {
        VersionInformation(
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE.toString()
        )
    }
}