package analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.dsl.MathsContext
import maths.core.dsl.plus

class VectorAnalysisDataTest: StringSpec({

    data class VectorTest(val x: Int, val y: Int)
    fun vector(x: Int, y: Int) = Const(VectorTest(x, y))

    "Vector addition carries forward to compound operations" {
        MathsContext.empty {
            infix fun Expr.shouldEqual(other: Expr) = this equal other shouldBe true
            infix fun Expr.shouldNotEqual(other: Expr) = this equal other shouldBe false

            withOperation("+") { a: VectorTest, b: VectorTest -> VectorTest(a.x + b.x, a.y + b.y) }

            val x by variable()
            val y by variable()

            x equate vector(1, 4)

            x shouldEqual vector(1, 4)
            x + y shouldEqual vector(1, 4) + y

            y equate vector(10, 10)

            y shouldEqual vector(10, 10)
            x + y shouldEqual vector(11, 14)
            x + y shouldNotEqual vector(11, 15)
        }
    }
})