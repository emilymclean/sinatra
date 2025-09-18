package cl.emilym.sinatra.network

import cl.emilym.sinatra.InvalidDigestException
import cl.emilym.sinatra.data.models.ShaDigest
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.github.aakira.napier.Napier
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.toByteArray
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.reflect.KClass

data class DigestedResponse<T>(
    val item: T,
    val digest: ShaDigest
)

fun <T> DigestedResponse<T>.validated(digest: ShaDigest): T = when (this.digest) {
    digest -> item
    else -> throw InvalidDigestException.mismatch(this.digest, digest)
}

class DigestedResponseConverterFactory(
    val types: Map<KClass<*>, ProtobufFactory<*>>
): Converter.Factory {

    class ProtobufDigestedResponseConverter(
        private val factory: ProtobufFactory<*>
    ): Converter.SuspendResponseConverter<HttpResponse, Any?> {
        @OptIn(ExperimentalStdlibApi::class)
        override suspend fun convert(result: KtorfitResult): Any? {
            return when (result) {
                is KtorfitResult.Failure -> throw result.throwable
                is KtorfitResult.Success -> {
                    val response = result.response.bodyAsChannel().toByteArray()
                    DigestedResponse(
                        factory(response),
                        SHA256().digest(response).toHexString()
                    )
                }
            }
        }
    }

    class ByteArrayDigestedResponseConverter: Converter.SuspendResponseConverter<HttpResponse, DigestedResponse<ByteArray>> {
        @OptIn(ExperimentalStdlibApi::class)
        override suspend fun convert(result: KtorfitResult): DigestedResponse<ByteArray> {
            return when (result) {
                is KtorfitResult.Failure -> throw result.throwable
                is KtorfitResult.Success -> {
                    val response = result.response.bodyAsChannel().toByteArray()
                    DigestedResponse(
                        response,
                        SHA256().digest(response).toHexString()
                    )
                }
            }
        }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type != DigestedResponse::class) return null
        if (typeData.typeArgs.isEmpty()) return null

        val type = typeData.typeArgs[0].typeInfo.type
        return when (type) {
            ByteArray::class -> {
                ByteArrayDigestedResponseConverter()
            }
            in types -> {
                Napier.d("Creating type converter for ${type.qualifiedName}")
                ProtobufDigestedResponseConverter(
                    types[type]!!
                )
            }
            else -> {
                Napier.d("No type converter for ${type.qualifiedName}, falling back to default")
                null
            }
        }
    }
}