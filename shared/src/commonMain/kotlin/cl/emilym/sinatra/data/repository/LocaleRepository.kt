package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.models.BCP47LanguageCode
import cl.emilym.sinatra.data.persistence.LocalePersistence
import org.koin.core.annotation.Factory

@Factory
class LocaleRepository(
    private val localePersistence: LocalePersistence
) {

    var languageCode: BCP47LanguageCode?
        get() = localePersistence.languageCode
        set(value) { localePersistence.languageCode = value }

    val acceptedLanguages get() = listOfNotNull(
        languageCode,
        languageCode?.split("-")?.getOrNull(0),
        "en-AU",
        "en",
        "*"
    ).mapIndexed { index, s ->
        "$s;q=${1.0 - (0.1 * index)}"
    }.joinToString(",")

}