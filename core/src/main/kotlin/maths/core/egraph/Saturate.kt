package maths.core.egraph

import maths.core.egraph.query.query
import maths.core.rewriting.RewriteRule

tailrec fun EGraph.saturate(rewriteRules: List<RewriteRule>, iterations: Int = 10) {
    if (iterations == 0) {
        println("Saturation iteration complete")
        return
    }

    println("Iterations remaining: $iterations")

    rewriteRules.forEach { rule ->
        rule.queries.forEach { query ->
            val matches = query(query)

            println("$rule found ${matches.size} matches")

            query.actions.forEach { action ->
                matches.forEach { eMatchResult ->
                    action.act(eMatchResult, this)
                }
            }
        }
    }

    if (eClassesToMerge.isEmpty()) {
        println("Saturation completed with $iterations remaining")
        return
    }

    rebuild()

    saturate(rewriteRules, iterations-1)
}
