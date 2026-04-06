package querying

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import maths.core.dsl.MathsContext
import maths.core.egraph.query.query

class QueryingTests : StringSpec({
    "A pattern condition should find a match" {
        MathsContext.empty {
            val x by variable()

            val results = eGraph.query {
                match { x }
            }

            results.shouldNotBeEmpty()
            results[0].matchedExpressions shouldBe mapOf("x" to x)
        }
    }
})