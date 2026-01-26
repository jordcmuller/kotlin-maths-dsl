package rewriting

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Add
import maths.core.ast.S
import maths.core.ast.Sub
import maths.core.ast.zero
import maths.core.ast.isNat
import maths.core.ast.nat
import maths.core.ast.s
import maths.core.dsl.maths
import maths.core.dsl.minus
import maths.core.dsl.plus
import maths.core.rewriting.rewrite

class SuccessorTests : StringSpec({
    "0 should be zero" {
        maths {
            0.nat shouldBe zero
            0.nat.isNat shouldBe true
        }
    }

    "create a successor of zero" {
        maths {
            val one = S(zero)
            one.isNat shouldBe true

            val anotherOne = zero.s
            one matches anotherOne shouldBe true

            val yetAnotherOne = 1.nat
            one matches yetAnotherOne shouldBe true

            one - anotherOne shouldBe Sub(1.nat, zero.s)
        }
    }

    "create a successor of a successor of zero" {
        maths {
            val two = S(S(zero))
            two.isNat shouldBe true

            two matches 1.nat.s shouldBe true
        }
    }

    "add two successors" {
        maths {
            val two = S(S(zero))
            val expressionForThree = two + 1.nat
            expressionForThree shouldBe Add(2.nat, 1.nat)
        }
    }

    "Rewrite S0 + 0 as S(0+0)" {
        maths {
            0.nat.s + 0.nat rewrite {
                S(0.nat) + 0.nat with S(0.nat + 0.nat)
            } matches S(0.nat + 0.nat) shouldBe true
        }
    }

    "Rewrite S(0) + 0 as S(0+0)" {
        maths {
            S(0.nat) + 0.nat rewrite {
                S(0.nat) + 0.nat with S(S(0.nat + 0.nat))
            } matches S(S(0.nat + 0.nat)) shouldBe true
        }
    }

    "Rewrite 2 + 3 as 5" {
        maths {
            2.nat + 3.nat rewrite {
                2.nat + 3.nat with 5.nat
            } matches 5.nat shouldBe true
        }
    }
})