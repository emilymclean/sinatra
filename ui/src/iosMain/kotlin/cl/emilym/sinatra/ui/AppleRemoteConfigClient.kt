package cl.emilym.sinatra.ui


interface TestProtocol

//@OptIn(ExperimentalForeignApi::class)
//fun firebaseRemoteConfig(): FIRRemoteConfig {
//    return FIRRemoteConfig.remoteConfig()
//}

//class AppleRemoteConfigWrapper @OptIn(ExperimentalForeignApi::class) constructor(
//    private val config: FIRRemoteConfig
//): RemoteConfigWrapper {
//
//    private val lock = Mutex()
//    private var loaded = false
//
//    @OptIn(ExperimentalForeignApi::class)
//    private suspend fun fetchAndActivate() {
//        return suspendCoroutine {
//            config.fetchAndActivateWithCompletionHandler { status, error ->
//                when (status) {
//                    FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusError ->
//                        it.resumeWithException(
//                            error?.let { DarwinException.fromNSError(it) } ?: DarwinException()
//                        )
//                    else -> it.resume(Unit)
//                }
//            }
//        }
//    }
//
//
//    private suspend fun load() {
//        if (loaded) return
//        lock.withLock {
//            if (loaded) return
//            fetchAndActivate()
//            loaded = true
//        }
//    }
//
//    @OptIn(ExperimentalForeignApi::class)
//    override suspend fun string(key: String): String? {
//        try {
//            load()
//        } catch(e: Exception) {
//            Napier.e(e)
//            return null
//        }
//        return config.configValueForKey(key).stringValue
//    }
//
//}