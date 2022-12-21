class SomeOtherClass(private val s: SomeClass) {
    fun someOtherValue(): String {
        return "some other value: (${s.someValue()})"
    }
}