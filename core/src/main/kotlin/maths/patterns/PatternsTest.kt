package maths.patterns

import maths.core.dsl.maths

fun main() {
    maths {
        val a by variable()
        val b by variable()
        val c by variable()

        val expr = (a + b) + c

        val pattern = ExprPattern.fromExpr(expr)

        ExprPattern.variable("a")

        val test1 = (a + b) + c // exact match

        val test2 = c + (b + a) // valid by commutativity + associativity

        val test3 = (a - b) + c

        println(pattern.accepts(test1)) // ✅ true
        println(pattern.accepts(test2)) // ✅ true
        println(pattern.accepts(test3)) // ❌ false
    }
}
