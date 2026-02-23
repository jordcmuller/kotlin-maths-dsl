import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.evaluate
import maths.core.dsl.c
import maths.core.dsl.div
import maths.core.dsl.function
import maths.core.dsl.invoke
import maths.core.dsl.lim
import maths.core.dsl.minus
import maths.core.dsl.plus
import maths.core.dsl.rangeTo
import maths.core.dsl.variable

class LimitTests: StringSpec({
    "A limit can be created" {
        val x by variable()
        val limit = lim(x..1) { x + 2 }

        limit.variable shouldBe x
        limit.approaching shouldBe 1.c
        limit.expression shouldBe x + 2
    }

    "A limit can be evaluated" {
        val x by variable()
        val limit = lim(x..1) { x }

        evaluate()
    }

    "Limits have algebra" {
        val h by variable()
        val x by variable()
        val f by function(x) { x }

//        val limitRules = lim(h..0) {}

        val derivativeLimit = lim(h..0) {
            (f(x + h) - f(x)) / h
        }
//    val calculusRewriteRules = listOf()
//    egg.saturate(calculusRewriteRules)

    }
})
