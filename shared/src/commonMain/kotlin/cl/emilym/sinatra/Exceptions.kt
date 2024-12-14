package cl.emilym.sinatra

class NoApiUrlException: Exception {

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    companion object {

        fun default(): NoApiUrlException {
            return NoApiUrlException("The service is currently unavailable, please try again later.")
        }

    }

}