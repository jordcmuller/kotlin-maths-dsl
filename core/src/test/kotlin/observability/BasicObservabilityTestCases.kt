package observability

import io.kotest.core.spec.style.StringSpec
import maths.core.dsl.MathsContext
import maths.core.dsl.plus
import maths.core.rewriting.dsl.rewriteRule

class BasicObservabilityTestCases: StringSpec({
    "" {
        MathsContext.empty {
            val m by variable()
            val n by variable()
            val k by variable()
            TODO()
        }
    }
})