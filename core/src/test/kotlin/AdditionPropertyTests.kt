import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Var
import maths.core.ast.VariableDeclaration
import maths.core.dsl.maths
import maths.core.state.Equivalence

class AdditionPropertyTests : StringSpec({
    "addition is commutative" {
        maths {
            val x by variable()
            val y by variable()

            (x + y eq y + x).equivalence shouldBe Equivalence.True
        }
   }

    "addition is associative" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            (x + (y + z) eq (x + y) + z).equivalence shouldBe Equivalence.True
        }
   }

    "addition is associative with commutation" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            (x + (y + z) eq (y + x) + z).equivalence shouldBe Equivalence.True
        }
   }

    "zero is the additive identity" { // TODO: this one will be interesting
        maths {
            val x by variable()

            (x + 0 eq x).equivalence shouldBe Equivalence.True
        }
    }
})