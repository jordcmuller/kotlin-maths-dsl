import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.maths
import maths.core.dsl.div
import maths.core.dsl.times

class MultiplicationPropertyTests : StringSpec({
    "multiplication is commutative" {
        maths {
            val x by variable()
            val y by variable()

            x * y equal y * x shouldBe true
        }
   }

    "multiplication is associative" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x * (y * z) equal (x * y) * z shouldBe true
            (x * y) * z equal x * (y * z) shouldBe true
        }
   }

    "multiplication is associative with commutation" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x * (y * z) equal (y * x) * z shouldBe true
        }
   }

    "one is the multiplicative identity" {
        maths {
            val x by variable()

            x * 1 equal x shouldBe true
        }
    }

    "Multiplying by a fraction is the same as dividing an integer" {
        maths {
            val x by variable()
            val y by variable()

            x / y equal x * (1 / y) shouldBe true
        }
    }
})