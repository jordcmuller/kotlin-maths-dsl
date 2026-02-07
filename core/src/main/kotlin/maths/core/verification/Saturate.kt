package maths.core.verification

import maths.core.ast.Expr
import maths.core.rewriting.RewriteRule

fun <ExprType> EGraph<ExprType>.add(rewrite: RewriteResult): EClass = when (rewrite) {
    is RRLeaf -> rewrite.eClass
    is RRBinary -> add(EBinary(add(rewrite.left), rewrite.operation.symbol, add(rewrite.right)))
}


tailrec fun saturate(eGraph: EGraph<Expr>, rewriteRules: List<RewriteRule>, maxIterations: Int = 10) {
    if (maxIterations == 0) return

    rewriteRules.forEach { rule ->
        val matches = eGraph.eMatch(rule.pattern)
        matches.forEach { eMatchResult ->
            val rewritten = rule.rewrite(eMatchResult) ?: return@forEach
            val rewriteEClass = eGraph.add(rewritten)
            eGraph.merge(eMatchResult.rootEClass, rewriteEClass)
        }
    }

    if (eGraph.worklist.isEmpty()) return

    eGraph.rebuild()

    saturate(eGraph, rewriteRules, maxIterations-1)
}
