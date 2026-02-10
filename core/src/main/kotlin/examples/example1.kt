package examples

import maths.core.dsl.maths
import maths.core.dsl.minus
import maths.core.dsl.plus
import maths.core.dsl.unaryMinus

fun main() {
    maths {
        val x by variable()
        val y by variable()

        if (x notEqual y) println("These are different variables, of course they're not equal")

        if (y + 1 notEqual x + 1) println("And these expressions are also not equal...")

        if (x + 1 - y equal -y + (1 + x)) println("But this equation is true thanks to the properties of addition!")

        x equate y

        if (y + 1 equal x + 1) println("After equating x and y, the previous equation is now true!")
    }
}
