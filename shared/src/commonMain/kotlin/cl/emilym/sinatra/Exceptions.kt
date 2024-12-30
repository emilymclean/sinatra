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

class RouterException: Exception {

    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    companion object {
        fun stopNotFound(): RouterException {
            return RouterException("No stops could not be found")
        }
        fun noJourneyFound(): RouterException {
            return RouterException("No valid journey could be found")
        }
    }

}