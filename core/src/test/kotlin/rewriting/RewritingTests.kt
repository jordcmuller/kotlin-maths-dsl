package rewriting

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.maths
import maths.core.rewriting.rewrite

class RewritingTests : StringSpec({
    "given x and the rule x = y, rewrite x as y" {
        maths {
            val x by variable()
            val y by variable()

            x rewrite (x eq y) shouldBe y
        }
    }

    "given x + y and the rule x + y = z, rewrite x + y as z + y" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x + y rewrite (x eq z) shouldBe z + y
        }
    }

    "given x + y and the rule x = z + y, rewrite x + y as z + y + y" {
        maths {
            val x by variable()
            val y by variable()
            val z by variable()

            x + y rewrite (x eq z + y) shouldBe z + y + y
        }
    }

    "given a * b * c and the associative and commutative properties of multiplication, rewrite a * b * c as b * (a * c)" {
        maths {
            val a by variable()
            val b by variable()
            val c by variable()

            a * b * c rewrite {
                a * b       with b * a          // commutative identity
                b * a * c   with b * (a * c)    // associative identity
            } shouldBe b * (a * c)
        }
    }
})