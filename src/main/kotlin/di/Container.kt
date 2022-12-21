package di

import kotlin.reflect.KCallable
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

sealed class ContainerEntry {
    data class Constructor<T>(val f: KCallable<T>) : ContainerEntry()
    data class Instance<T>(val instance: T) : ContainerEntry()
}

class Container {

    private val map = mutableMapOf<KType, ContainerEntry>()

    fun <T : Any> add(c: KCallable<T>) {
        map[c.returnType] = ContainerEntry.Constructor(c)
    }

    fun <T : Any> provide(v: T) {
        map[v::class.starProjectedType] = ContainerEntry.Instance(v)
    }

    private fun buildByType(type: KType): Any {
        return when (val result = map[type]!!) {
            is ContainerEntry.Instance<*> -> result.instance!!
            is ContainerEntry.Constructor<*> -> {
                val parameters = result.f.parameters.map { param -> buildByType(param.type) }
                val instance = result.f.call(*parameters.toTypedArray())
                map[type] = ContainerEntry.Instance(instance)
                return instance!!
            }
        }
    }

    fun <T> run(f: KCallable<T>): T {
        val callableParameters = f.parameters
        val parameters = callableParameters.map { parameter ->
            val type = parameter.type
            buildByType(type)
        }
        return f.call(*parameters.toTypedArray())
    }

}