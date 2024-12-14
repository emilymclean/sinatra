package cl.emilym.sinatra.ui

import platform.Foundation.NSError

class DarwinException: Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    companion object {

        fun fromNSError(error: NSError): DarwinException {
            return DarwinException(error.localizedDescription)
        }

    }
}