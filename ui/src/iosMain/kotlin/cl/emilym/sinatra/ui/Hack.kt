// See https://github.com/google/ksp/issues/929#issuecomment-1826321787
package org.koin.ksp.generated // package should be same as your generated code

import org.koin.core.module.Module

@Suppress("UnusedReceiverParameter")
val Any.module: Module
    get() = throw RuntimeException("Koin module was not generated. Add ksp for all your targets")