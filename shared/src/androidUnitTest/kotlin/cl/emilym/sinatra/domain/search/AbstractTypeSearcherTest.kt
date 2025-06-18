package cl.emilym.sinatra.domain.search

import kotlin.jvm.java
import kotlin.reflect.full.functions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AbstractTypeSearcherTest {

    private open class TestTypeSearcher(
        override val permitIncompleteMatches: Boolean = false
    ) : AbstractTypeSearcher<String>() {
        override fun fields(t: String): List<String> {
            return listOf(t)
        }

        override suspend fun invoke(tokens: List<String>) = throw Exception()

        override fun wrap(item: String) = throw Exception()
    }

    private val typeSearcher = TestTypeSearcher()
    private val typeSearcherPermitIncomplete = TestTypeSearcher(true)

    @Test
    fun `match should return RankableResult when all tokens are matched`() {
        val tokens = listOf("test", "example")
        val item = "This is a test example"
        val matchFraction = tokens.sumOf { it.length } / item.length.toDouble()

        val matchMethod = typeSearcher::class.java.superclass.declaredMethods.first { it.name == "match" }
        matchMethod.isAccessible = true

        val result = matchMethod.invoke(typeSearcher, tokens, item) as RankableResult<String>?

        assertEquals(item, result?.result)
        assertEquals(matchFraction, result?.score)
    }

    @Test
    fun `match should return null when not all tokens are matched`() {
        val tokens = listOf("test", "example", "missing")
        val item = "This is a test example"

        val matchMethod = typeSearcher::class.java.superclass.declaredMethods.first { it.name == "match" }
        matchMethod.isAccessible = true

        val result = matchMethod.invoke(typeSearcher, tokens, item) as RankableResult<String>?

        assertNull(result)
    }

    @Test
    fun `match should return when not all tokens are matched and permitIncompleteMatches true`() {
        val tokens = listOf("test", "example", "missing")
        val item = "This is a test example"

        val matchMethod = typeSearcherPermitIncomplete::class.java.superclass.declaredMethods.first { it.name == "match" }
        matchMethod.isAccessible = true

        val result = matchMethod.invoke(typeSearcherPermitIncomplete, tokens, item) as RankableResult<String>?

        assertNotNull(result)
    }

    @Test
    fun `matchScore should calculate the correct score`() {
        println("Properties = ${typeSearcher::class.functions.map { it.name }}")
        val matchScoreMethod = typeSearcher::class.java.superclass.declaredMethods.first { it.name =="matchScore" }
        matchScoreMethod.isAccessible = true

        val field = "This is a test"
        val matchingTokens = listOf("test", "is")

        val score = matchScoreMethod.invoke(typeSearcher, matchingTokens, field) as Double

        val expectedScore = (4 + 2).toDouble() / field.length.toDouble()
        assertEquals(expectedScore, score)
    }

    @Test
    fun `exclusiveMatch should return only the matched tokens`() {
        val exclusiveMatchMethod = typeSearcher::class.java.superclass.declaredMethods.first { it.name =="exclusiveMatch" }
        exclusiveMatchMethod.isAccessible = true

        val tokens = listOf("test", "example", "this")
        val field = "This is a test example"

        val matches = exclusiveMatchMethod.invoke(typeSearcher, tokens, field.lowercase()) as List<String>

        assertEquals(listOf("example", "test", "this"), matches)
    }

    @Test
    fun `search should return sorted RankableResults`() {
        val items = listOf("test example", "another test", "example test")
        val tokens = listOf("test", "example")

        val searchMethod = typeSearcher::class.java.superclass.declaredMethods.first { it.name =="search" }
        searchMethod.isAccessible = true

        val results = searchMethod.invoke(typeSearcher, tokens, items) as List<RankableResult<String>>

        assertEquals(2, results.size)
        assertEquals("test example", results[0].result)
        assertEquals("example test", results[1].result)
    }

    @Test
    fun `scoreMultiplier should adjust the final score`() {
        val searcher = object : TestTypeSearcher() {
            override fun scoreMultiplier(item: String) = 2.0
        }

        val tokens = listOf("test", "example")
        val item = "test example"
        val matchFrac = tokens.sumOf { it.length } / item.length.toDouble()

        val matchMethod = searcher::class.java.superclass.superclass.declaredMethods.first { it.name == "match" }
        matchMethod.isAccessible = true

        val result = matchMethod.invoke(searcher, tokens, item) as RankableResult<String>?

        assertEquals(matchFrac * 2.0, result?.score) // Score should be doubled by scoreMultiplier
    }
}
