package cl.emilym.sinatra.android

import android.app.Application
import cl.emilym.sinatra.room.databaseBuilderModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                AppModule().module,
                databaseBuilderModule,
            )
        }
    }
}