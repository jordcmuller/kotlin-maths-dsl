import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.maths

class BaseContextIntegerTests : StringSpec({
    "2 should not change the context" {
        val context = maths {
            2.c
        }

        context.state.errors shouldBe emptyList()
        context.state.definedVars shouldBe emptySet()
        context.state.statements shouldBe emptyList()
    }


    "2 + 4 should not change the context" {
        val context = maths {
            2.c + 4.c
        }

        context.state.errors shouldBe emptyList()
        context.state.definedVars shouldBe emptySet()
    }
})