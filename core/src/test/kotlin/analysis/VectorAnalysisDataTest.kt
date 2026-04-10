package analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Const
import maths.core.dsl.MathsContext
import maths.core.dsl.plus

class VectorAnalysisDataTest: StringSpec({

    data class VectorTest(val x: Int, val y: Int)

    "Vector addition carries forward to compound operations" {
        MathsContext.empty {
            withOperation("+") { a: VectorTest, b: VectorTest -> VectorTest(a.x + b.x, a.y + b.y) }

            val x by variable()
            val y by variable()

            x equate Const(VectorTest(1, 4))

            x equal Const(VectorTest(1, 4)) shouldBe true
            x + y equal Const(VectorTest(1, 4)) + y shouldBe true

            y equate Const(VectorTest(10, 10))

            y equal Const(VectorTest(10, 10)) shouldBe true
            x + y equal Const(VectorTest(11, 14)) shouldBe true
            x + y equal Const(VectorTest(11, 15)) shouldBe false
        }
    }
})