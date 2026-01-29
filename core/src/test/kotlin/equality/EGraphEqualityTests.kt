package equality

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.maths
import maths.core.dsl.plus
import maths.core.dsl.times

class EGraphEqualityTests : StringSpec({
    "Equating two variables makes their dependent expressions equal" {
        maths {
            val x by variable()
            val y by variable()

            x + 1 notEqual y + 1 shouldBe true
            x equate y
            x + 1 equal y + 1 shouldBe true
        }
    }

    "Equality is reflexive: For every x, one has x = x." {
        maths {
            val x by variable()
            x equal x shouldBe true
        }
    }

    "Equality is symmetric: For every a and b, if a = b, then b = a." {
        maths {
            val a by variable()
            val b by variable()
            b equal a shouldBe false
            a equate b
            b equal a shouldBe true
        }
    }

    "Equality is transitive: For every a, b, and c, if a = b and b = c, then a = c." {
        maths {
            val a by variable()
            val b by variable()
            val c by variable()

            a equal c shouldBe false

            a equate b

            a equal c shouldBe false

            b equate c

            a equal c shouldBe true
        }
    }

    "Additive identity with reflexivity" {
        maths {
            val x by variable()

            x + 0 equate x

            x equal x + 0 shouldBe true
            x + 0 equal x + 0 shouldBe true
            x + 0 equal x shouldBe true
        }
    }

    "All commutative and associative combinations of a * b * c" {
        maths {
            val a by variable()
            val b by variable()
            val c by variable()

            a * b * c equal (a * b) * c shouldBe true
            a * b * c equal (a * c) * b shouldBe true
            a * b * c equal (b * a) * c shouldBe true
            a * b * c equal (b * c) * a shouldBe true
            a * b * c equal (c * b) * a shouldBe true
            a * b * c equal (c * a) * b shouldBe true
            a * b * c equal a * (b * c) shouldBe true
            a * b * c equal a * (c * b) shouldBe true
            a * b * c equal b * (a * c) shouldBe true
            a * b * c equal b * (c * a) shouldBe true
            a * b * c equal c * (b * a) shouldBe true
            a * b * c equal c * (a * b) shouldBe true
        }
    }

    "The distributive property holds" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x * (y + z) equal x * y + x * z shouldBe true
            (y + z) * x equal x * y + x * z shouldBe true

            x * y + x * z equal x * (y + z) shouldBe true
            x * y + x * z equal (y + z) * x shouldBe true
        }
    }
})