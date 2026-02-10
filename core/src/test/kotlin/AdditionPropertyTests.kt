import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.maths
import maths.core.dsl.minus
import maths.core.dsl.plus
import maths.core.dsl.unaryMinus

class AdditionPropertyTests : StringSpec({
    "addition is commutative" {
        maths {
            val x by variable()
            val y by variable()

            x + y eq y + x shouldBe true
        }
   }

    "addition is associative" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x + (y + z) eq (x + y) + z shouldBe true
            (x + y) + z eq x + (y + z) shouldBe true
        }
   }

    "addition is associative with commutation" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x + (y + z) equal (y + x) + z shouldBe true
        }
   }

    "zero is the additive identity" {
        maths {
            val x by variable()

            x + 0 equal x shouldBe true
        }
    }

    "Adding a negative is the same as subtracting a positive" {
        maths {
            val x by variable()
            val y by variable()

            x - y equal x + (-y) shouldBe true
        }
    }
})