package cl.emilym.sinatra.android.widget.base

import cl.emilym.sinatra.SharedModule
import cl.emilym.sinatra.ui.UIModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [SharedModule::class, UIModule::class])
@ComponentScan
class WidgetModule