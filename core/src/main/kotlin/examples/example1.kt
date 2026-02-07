package examples

import maths.core.dsl.maths
import maths.core.dsl.plus

fun main() {
    maths {
        val x by variable()
        val y by variable()

        if (x notEqual y) println("These are different variables, of course they're not equal")

        val expr1 = y + 1
        val expr2 = x + 1

        if (expr1 notEqual expr2) println("These expressions are also not equal...")

        x equate y

        if (expr1 equal expr2) println("But now they are!")
    }
}
