import di.Container

fun main(args: Array<String>) {
    val container = Container()
    container.add(::SomeClass)
    container.add(::SomeOtherClass)
    container.provide(44)
    container.provide("just a string")
    val result = container.run(::run)
    println("result: $result")
}

fun run(sc: SomeOtherClass, num: Int): Float {
    println("${sc.someOtherValue()} - $num")
    return 3.14f
}
