package analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Expr
import maths.core.dsl.MathsContext
import maths.core.dsl.c
import maths.core.dsl.function
import maths.core.dsl.plus

class FunctionAnalysisDataTest: StringSpec({

    "Some basic function stuff" {
        MathsContext.empty {
            infix fun Expr.shouldEqual(other: Expr) = this equal other shouldBe true
            infix fun Expr.shouldNotEqual(other: Expr) = this equal other shouldBe false

            withOperation("+") { a: Int, b: Int -> a + b }

            val x by variable()
            val y by variable()

            val f by function(x) { x + 1 }

            f shouldEqual x + 1
        }
    }
})