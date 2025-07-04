package cl.emilym.sinatra.ui.savedstate

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import kotlin.reflect.KClass

class ScreenModelStateParametersHolder(
    initialValues: ParametersDefinition? = null,
    val screenModelState: ScreenModelState
): ParametersHolder(
    initialValues?.invoke()?.values?.toMutableList() ?: mutableListOf()
) {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        if (clazz == ScreenModelState::class) return screenModelState as T
        return super.elementAt(i, clazz)
    }

    override fun <T> getOrNull(clazz: KClass<*>): T? {
        if (clazz == ScreenModelState::class) return screenModelState as T
        return super.getOrNull(clazz)
    }
}