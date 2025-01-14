package cl.emilym.sinatra.data.persistence

import cl.emilym.sinatra.data.models.BCP47LanguageCode
import org.koin.core.annotation.Single

@Single
class LocalePersistence {
    var languageCode: BCP47LanguageCode? = null
}