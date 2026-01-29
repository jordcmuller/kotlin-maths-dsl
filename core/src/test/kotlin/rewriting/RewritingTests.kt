package rewriting

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.maths
import maths.core.dsl.plus
import maths.core.dsl.squared
import maths.core.dsl.times
import maths.core.rewriting.rewrite

class RewritingTests : StringSpec({
    "given x and the rule x = y, rewrite x as y" {
        maths {
            val x by variable()
            val y by variable()

            x equate y

            x rewrite (x eq y) shouldBe y
        }
    }

    "given x + y and the rule x + y = z, rewrite x + y as z + y" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x equate z

            x + y rewrite (x eq z) shouldBe z + y
        }
    }

    "given x + y and the rule x = z + y, rewrite x + y as z + y + y" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x equate z + y

            x + y rewrite (x eq z + y) shouldBe z + y + y
        }
    }

    "given a * b * c and the associative and commutative properties of multiplication, rewrite a * b * c as b * (a * c)" {
        maths {
            val a by variable()
            val b by variable()
            val c by variable()

            a equal b shouldBe false

            a * b * c rewrite {
                a * b       with b * a          // commutative identity
                b * a * c   with b * (a * c)    // associative identity
            } shouldBe b * (a * c)
        }
    }

    "factorize x^2 + 2x + 1" {
        maths {
            val x by variable()

            x.squared + 2 * x + 1 rewrite {
                2 * x                       with (1.c + 1.c) * x

                (1.c + 1.c) * x             with 1 * x + 1 * x

                1 * x + 1 * x               with x + x

                x.squared + (x + x) + 1     with (x.squared + x) + (x + 1)

                x + 1                       with 1 * (x + 1)

                x.squared + x               with x * (x + 1)

                x * (x + 1) + 1 * (x + 1)   with (x + 1) * (x + 1)

                (x + 1) * (x + 1)           with (x + 1).squared
            } shouldBe (x + 1).squared
        }
    }
})