package maths.core.verification

import maths.core.ast.Expr

tailrec fun saturate(eGraph: EGraph<Expr>, rewriteRules: List<RewriteRule>) {
    rewriteRules.forEach { rule ->
        val matches = eMatch(eGraph, rule.structure)
        matches.forEach { match ->
            val matchEClass = eGraph.add(match)
            val rewritten = rule.rewrite(match) ?: return@forEach
            val rewriteEClass = eGraph.add(rewritten)
            eGraph.merge(matchEClass, rewriteEClass)
        }
    }

    if (eGraph.worklist.isEmpty()) return

    eGraph.rebuild()

    saturate(eGraph, rewriteRules)
}
