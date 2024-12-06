package cl.emilym.sinatra.domain

import org.koin.core.annotation.Factory

@Factory
class Tokenizer {
    private val splitCharacters = arrayOf(' ', '.', ',', ';', ':')

    fun tokenize(text: String): List<String> {
        val tokens = mutableListOf<String>()
        var currentToken = ""

        fun addCurrent() {
            if (currentToken == "") return
            tokens += currentToken
            currentToken = ""
        }


        val chars = text.iterator()
        while (chars.hasNext()) {
            val c = chars.nextChar()
            if (c in splitCharacters) {
                addCurrent()
                continue
            }
            currentToken += c
        }
        addCurrent()

        return tokens
    }

}