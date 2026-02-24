package maths.core.egraph

import maths.core.egraph.querying.PatternCondition
import maths.core.rewriting.RewriteRule
import maths.core.rewriting.eMatcherToExpr

tailrec fun EGraph.saturate(rewriteRules: List<RewriteRule>, maxIterations: Int = 10) {
    if (maxIterations == 0) return

    rewriteRules.forEach { rule ->
        val matches = processQuery(rule.query)
        matches.forEach { eMatchResult ->
            // Todo: handle the different ways that the rewrite rules will change the e-graph
            val original = rule.query.premises.first() as PatternCondition
            val originalEClass = add(original.pattern.eMatcherToExpr(eMatchResult))

            val rewritten = rule.rewrite(eMatchResult) ?: return@forEach
            val rewriteEClass = add(rewritten)

            merge(originalEClass, rewriteEClass)
        }
    }

    if (worklist.isEmpty()) return

    rebuild()

    saturate(rewriteRules, maxIterations-1)
}
