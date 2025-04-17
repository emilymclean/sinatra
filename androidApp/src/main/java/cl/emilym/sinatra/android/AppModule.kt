package cl.emilym.sinatra.android

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.android.widget.WidgetModule
import cl.emilym.sinatra.ui.UIModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module(includes = [SharedModule::class, UIModule::class, WidgetModule::class])
@ComponentScan
class AppModule

val nativeModule = module {
    single {
        BuildInformation(
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE.toString()
        )
    }
}