package examples

import maths.core.dsl.c
import maths.core.dsl.maths
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.rewriting.dsl.rewriteRule
import maths.core.rewriting.dsl.where

fun main() {
    maths {
        state.rewriteRules.clear()

        val m by variable()
        val n by variable()
        val k by variable()

        val additiveCommutativity by rewriteRule { m + n to n + m }
        val multiplicativeCommutativity by rewriteRule { m * n to n * m }

        val additiveAssociativity by rewriteRule { (m + n) + k to n + (m + k) }
        val multiplicativeAssociativity by rewriteRule { (m * n) * k to n * (m * k) }

        val distributivity by rewriteRule { m * (n + k) to m * n + m * k }

        val additiveIdentity by rewriteRule { m + 0 to m }
        val multiplicativeIdentity by rewriteRule { m * 1 to m }

        val multiplicativeCancellation by rewriteRule { m to n } where { k notEqual 0.c and (k * m equal k * n) }

        state.rewriteRules += additiveCommutativity
        state.rewriteRules += multiplicativeCommutativity
        state.rewriteRules += additiveAssociativity
        state.rewriteRules += multiplicativeAssociativity
        state.rewriteRules += distributivity
        state.rewriteRules += additiveIdentity
        state.rewriteRules += multiplicativeIdentity
        state.rewriteRules += multiplicativeCancellation

        m + k equate n + k

        val l by variable()

        k + l equate 0

        require((m + k) + l equal (n + k) + l)
        require(m + (k + l) equal n + (k + l))

        require(m + 0 equal n + 0)

        require(m equal n)
    }
}
