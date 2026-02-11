package equality

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.maths
import maths.core.dsl.plus
import maths.core.dsl.times

class EqualityPropertiesTests : StringSpec({
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
            a equate b
            b equal a shouldBe true
        }
    }

    "Equality is transitive: For every a, b, and c, if a = b and b = c, then a = c." {
        maths {
            val a by variable()
            val b by variable()
            val c by variable()
            a equate b
            b equate c
            a equal c shouldBe true
        }
    }

    "Additive identity with reflexivity" {
        maths {
            val x by variable()
            x + 0 equal x shouldBe true
            x equal x + 0 shouldBe true
        }
    }

    "a * b * c = b * (a * c)" {
        maths {
            val a by variable()
            val b by variable()
            val c by variable()

            a * b * c equal b * (a * c) shouldBe true
        }
    }

    "Equality allows for substitution" {
        maths {
//            (1.c eq 1.c).equivalence shouldBe Equivalence.True
        }
    }

    "Equality holds for the function application property" {
        maths {
//            (1.c eq 2.c).equivalence shouldBe Equivalence.False
        }
    }
})