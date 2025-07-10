package cl.emilym.sinatra

import kotlinx.datetime.Clock
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module
@ComponentScan
class SharedModule

val manualModule = module {
    factory<Clock> { kotlin.time.Clock.System }
}