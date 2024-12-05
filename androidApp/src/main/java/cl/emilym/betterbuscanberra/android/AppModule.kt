package cl.emilym.betterbuscanberra.android

import cl.emilym.betterbuscanberra.SharedModule
import cl.emilym.sinatra.ui.UIModule
import org.koin.core.annotation.Module

@Module(includes = [SharedModule::class, UIModule::class])
class AppModule