package maths.core.verification

import maths.core.rewriting.RewriteRule

tailrec fun EGraph.saturate(rewriteRules: List<RewriteRule>, maxIterations: Int = 10) {
    if (maxIterations == 0) return

    rewriteRules.forEach { rule ->
        val matches = eMatch(rule.pattern)
        matches.forEach { eMatchResult ->
            val rewritten = rule.rewrite(eMatchResult) ?: return@forEach
            val rewriteEClass = add(rewritten)
            merge(eMatchResult.rootEClass, rewriteEClass)
        }
    }

    if (worklist.isEmpty()) return

    rebuild()

    saturate(rewriteRules, maxIterations-1)
}
