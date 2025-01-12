package cl.emilym.sinatra.data.models

import com.google.transit.realtime.TranslatedString

private val FALLBACK_LANGUAGES = listOf("en-AU", "en-US", "en")

interface LocalizableString {
    fun get(preferredLanguage: BCP47LanguageCode? = null): String
}

data class TranslatedStringLocalizableString(
    val translatedString: TranslatedString
): LocalizableString {

    override fun get(preferredLanguage: BCP47LanguageCode?): String {
        val translation = translatedString.translation
        return (
            translation.firstOrNull { it.language == (preferredLanguage ?: FALLBACK_LANGUAGES[0]) } ?:
            FALLBACK_LANGUAGES.firstNotNullOfOrNull { fbl -> translation.firstOrNull { it.language == fbl } } ?:
            translation.firstOrNull { it.language == null } ?:
            translation.first()
        ).text
    }
}