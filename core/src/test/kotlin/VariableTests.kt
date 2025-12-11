import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import maths.core.ast.VariableDeclaration
import maths.core.dsl.maths

class VariableTests : StringSpec({
    "defining a variable should change the context" {
        val context = maths {
            val x by variable()
        }

        context.state.errors shouldBe emptyList()
        context.state.definedVars shouldBe setOf("x")
        context.state.statements shouldBe listOf(VariableDeclaration("x"))
    }

    "defining two different variables should change the context" {
        val context = maths {
            val x by variable()
            val y by variable()
        }

        context.state.errors shouldBe emptyList()
        context.state.definedVars shouldBe setOf("x", "y")
        context.state.statements shouldBe listOf(VariableDeclaration("x"), VariableDeclaration("y"))
    }

    "defining the same variable twice should output an error" {
        val context = maths {
            val x by variable()
            val X by variable("x")
        }

        context.state.errors.size shouldBe 1
        context.state.errors.first().toString() shouldBe "'let x' is invalid due to 'Variable 'x' is already defined'"
        context.state.definedVars shouldBe setOf("x")
        context.state.statements shouldBe listOf(VariableDeclaration("x"), VariableDeclaration("x"))
    }
})