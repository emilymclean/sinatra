package cl.emilym.sinatra.android

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.ui.UIModule
import org.koin.core.annotation.Module

@Module(includes = [SharedModule::class, UIModule::class])
class AppModule