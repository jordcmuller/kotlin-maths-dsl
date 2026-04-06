package equality

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.dsl.maths
import maths.core.ast.Equivalence
import maths.core.dsl.c
import maths.core.dsl.plus

class IntegerEqualityTests : StringSpec({
    "A constant should be equal to itself" {
        maths {
            (1.c eq 1.c).equivalence shouldBe Equivalence.True
        }
    }

    "1 = 2 should be false, add an equality statement to the context, declare no variables, and list an error" {
        val context = maths {
            val e = 1.c eq 2.c

            e.equivalence shouldBe Equivalence.False
        }

        context.errors.first().toString() shouldBe "'1 = 2' is invalid due to 'Equation is not true'"
        context.statements shouldBe listOf(Equation(
            Const(1),
            Const(2)
        ))
    }

    "2 = 2 should be true, add an equality statement to the context, declare no variables, and list no errors" {
        val context = maths {
            val e = 2.c eq 2.c

            e.equivalence shouldBe Equivalence.True
        }

        context.errors shouldBe emptyList()
        context.statements shouldBe listOf(Equation(
            Const(2),
            Const(2)
        ))
    }

    "2 + 2 = 6 should be false, add an equality statement to the context and list an error" {
        val context = maths {
            val e = 2.c + 2.c eq 6.c

            e.equivalence shouldBe Equivalence.False
        }

        context.errors.first().toString() shouldBe "'2 + 2 = 6' is invalid due to 'Equation is not true'"
        context.statements shouldBe listOf(Equation(
            Add(Const(2), Const(2)),
            Const(6)
        ))
    }

    "2 + 2 = 4 should be true, add an equality statement to the context, declare no variables, and list no errors" {
        val context = maths {
            val e = 2.c + 2.c eq 4.c

            e.equivalence shouldBe Equivalence.True
        }

        context.errors shouldBe emptyList()
        context.statements shouldBe listOf(Equation(
            Add(Const(2), Const(2)),
            Const(4)
        ))
    }

    "2 + 2 = 2 + 2 should be true, add an equality statement to the context, declare no variables, and list no errors" {
        val context = maths {
            val e = 2.c + 2.c eq 2.c + 2.c

            e.equivalence shouldBe Equivalence.True
        }

        context.errors shouldBe emptyList()
        context.statements shouldBe listOf(Equation(
            Add(Const(2), Const(2)),
            Add(Const(2), Const(2)),
        ))
    }

//    "4 = 6 should add an equality statement to the context and list an error" {
//        val context = maths {
//            2.c + 4.c eq 6.c
//        }
//
//        context.errors shouldBe emptyList()
//        context.definedVars shouldBe emptySet()
//    }
//
//    "2 + 4 = 7 should execute and output error" {
//        val context = maths {
//            2.c + 4.c eq 7.c
//        }
//
//        context.errors.size shouldBe 1
//        context.errors[0].message shouldBe "Equality conflicts: 2 + 4 != 7"
//    }
})