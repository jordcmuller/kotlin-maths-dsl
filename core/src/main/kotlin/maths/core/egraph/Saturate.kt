package maths.core.egraph

import maths.core.egraph.query.query
import maths.core.rewriting.RewriteRule

tailrec fun EGraph.saturate(rewriteRules: List<RewriteRule>, maxIterations: Int = 10) {
    if (maxIterations == 0) return

    rewriteRules.forEach { rule ->
        val matches = query(rule.query)
        matches.forEach { eMatchResult ->
            rule.actions.forEach { action -> action.act(eMatchResult, this) }
        }
    }

    if (worklist.isEmpty()) return

    rebuild()

    saturate(rewriteRules, maxIterations-1)
}
