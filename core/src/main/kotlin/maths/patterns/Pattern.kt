package maths.patterns

fun interface Pattern<T> {
    fun accepts(value: T): Boolean
}

open class PatternCondition<T>(
    private val description: String,
    private val matcher: (T) -> Boolean
) : Pattern<T> {
    override fun accepts(value: T): Boolean = matcher(value)
    override fun toString() = description
}
