package examples

import maths.core.dsl.MathsContext
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.rewriting.dsl.invoke
import maths.core.rewriting.dsl.rewriteRule
import maths.core.rewriting.dsl.where

fun main() {
    MathsContext.empty {
        val m by variable()
        val n by variable()
        val k by variable()

        withRule { rewriteRule("additiveCommutativity") { m + n to n + m } }
        withRule { "multiplicativeCommutativity" { m * n to n * m } }

        withRule { "additiveAssociativity" { (m + n) + k to n + (m + k) } }
        withRule { "multiplicativeAssociativity" { (m * n) * k to n * (m * k) } }

        withRule { "distributivity" { m * (n + k) to m * n + m * k } }

        // todo: figure out why identity rules are exploding
//        withRule { "additiveIdentity" { m + 0 to m } }
//        withRule { "multiplicativeIdentity" { m * 1 to m } }

        // TODO: figure out a better class structure for the rewrite rule DSL
        val multiplicativeCancellation by m to n where { k notEqual 0.c and (k * m equal k * n) }
        withRule { multiplicativeCancellation }

        m + k equate n + k

        val l by variable()

        k + l equate 0

        require((m + k) + l equal (n + k) + l)
        require(m + (k + l) equal n + (k + l))

        require(m + 0 equal n + 0)

        require(m equal n)
    }
}
