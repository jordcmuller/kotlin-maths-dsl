package analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.MathsContext
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.rewriting.dsl.provideDelegate

class IntAnalysisDataTest: StringSpec({
    "Integer addition carries forward to compound operations" {
        MathsContext.empty {
            withOperation("+") { a: Int, b: Int -> a + b }

            // register axioms
            val x by variable()
            val y by variable()
            val additiveCommutativity by x + y to y + x
            withRule { additiveCommutativity }

            x + y equal y + x shouldBe true

            x equate 1

            x equal 1 shouldBe true
            x + y equal y + 1 shouldBe true

            y equate 3

            y equal 3 shouldBe true
            x + y equal 4 shouldBe true
            x + y equal 5 shouldBe false

            1.c + 2.c + y equal x + x + x + x + x + x
        }
    }
})