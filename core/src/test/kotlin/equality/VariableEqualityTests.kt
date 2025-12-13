package equality

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Var
import maths.core.ast.VariableDeclaration
import maths.core.dsl.maths
import maths.core.state.Equivalence

class VariableEqualityTests : StringSpec({
    "x defined as an unknown and equated to 2 should make x equal to 2" {
        val context = maths {
            val x by variable()

            (x eq 2.c).equivalence shouldBe Equivalence.Unknown

            (x eq 2.c).equivalence shouldBe Equivalence.True
        }

        context.state.definedVars shouldBe setOf("x")
        context.state.statements shouldBe listOf(
            VariableDeclaration("x"),
            Equation(
                Var("x"),
                Const(2.0)
            ),
            Equation(
                Var("x"),
                Const(2.0)
            )
        )
        context.state.variableValues["x"] shouldBe Const(2.0)
    }

    "x defined as an unknown and equated to 2, y is defined as unknown and equated to 3, comparing them should be false" {
        val context = maths {
            val x by variable()
            val y by variable()

            (x eq 2.c).equivalence shouldBe Equivalence.Unknown
            (x eq 2.c).equivalence shouldBe Equivalence.True

            (3.c eq y).equivalence shouldBe Equivalence.Unknown
            (3.c eq y).equivalence shouldBe Equivalence.True

            (x eq y).equivalence shouldBe Equivalence.False
            (y eq x).equivalence shouldBe Equivalence.False
        }

        context.state.definedVars shouldBe setOf("x", "y")
        context.state.statements shouldBe listOf(
            VariableDeclaration("x"),
            VariableDeclaration("y"),
            Equation(
                Var("x"),
                Const(2.0)
            ),
            Equation(
                Var("x"),
                Const(2.0)
            ),
            Equation(
                Const(3.0),
                Var("y")
            ),
            Equation(
                Const(3.0),
                Var("y")
            ),
            Equation(
                Var("x"),
                Var("y")
            ),
            Equation(
                Var("y"),
                Var("x")
            )
        )
        context.state.variableValues["x"] shouldBe Const(2.0)
        context.state.variableValues["y"] shouldBe Const(3.0)
    }

    "define x and it should equal itself" {
        val context = maths {
            val x by variable()

            (x eq x).equivalence shouldBe Equivalence.True
        }

        context.state.definedVars shouldBe setOf("x")
        context.state.statements shouldBe listOf(
            VariableDeclaration("x"),
            Equation(
                Var("x"),
                Var("x")
            )
        )
    }

    "A variable should be equal to itself" {
        maths {
            val x by variable()
            (x eq x).equivalence shouldBe Equivalence.True
        }
    }

    "An unknown variable equated to another unknown variable should give an unknown result" {
        maths {
            val x by variable()
            val y by variable()
            (x eq y).equivalence shouldBe Equivalence.Unknown
        }
    }

    "Check that the same numeric expression is equal" {
        maths {
            (1.c + 2.c eq 1.c + 2.c).equivalence shouldBe Equivalence.True
        }
    }
})