package cl.emilym.sinatra.component.base

import com.arkivanov.decompose.ComponentContext
import org.koin.core.Koin

interface KoinComponentContext: ComponentContext {
    val koin: Koin
}

interface SinatraComponentContext: KoinComponentContext, ComponentContext {

    companion object {
        fun create(
            componentContext: ComponentContext,
            koin: Koin
        ): SinatraComponentContext {
            return DefaultSinatraComponentContext(
                componentContext,
                koin
            )
        }
    }

}

internal class DefaultSinatraComponentContext(
    componentContext: ComponentContext,
    override val koin: Koin
): ComponentContext by componentContext, SinatraComponentContext