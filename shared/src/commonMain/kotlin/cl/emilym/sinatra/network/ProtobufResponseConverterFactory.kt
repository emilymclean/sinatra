package cl.emilym.sinatra.network

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.github.aakira.napier.Napier
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.toByteArray
import kotlin.reflect.KClass

typealias ProtobufFactory<T> = (arr: ByteArray) -> T

class ProtobufResponseConverterFactory(
    val types: Map<KClass<*>, ProtobufFactory<*>>
) : Converter.Factory {

    class ProtobufSuspendResponseConverter(
        val factory: ProtobufFactory<*>
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any? {
            return when (result) {
                is KtorfitResult.Failure -> throw result.throwable
                is KtorfitResult.Success -> factory(result.response.bodyAsChannel().toByteArray())
            }
        }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        Napier.d("Determining if there is a protobuf converter for ${typeData.typeInfo.type.qualifiedName}")
        return when {
            typeData.typeInfo.type in types -> {
                Napier.d("Creating type converter for ${typeData.typeInfo.type.qualifiedName}")
                ProtobufSuspendResponseConverter(
                    types[typeData.typeInfo.type]!!
                )
            }
            else -> {
                Napier.d("No type converter for ${typeData.typeInfo.type.qualifiedName}, falling back to default")
                null
            }
        }
    }
}