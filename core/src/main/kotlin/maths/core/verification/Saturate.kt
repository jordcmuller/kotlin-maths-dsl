package maths.core.verification

import maths.core.ast.Expr
import maths.core.rewriting.RewriteRule

fun <ExprType> EGraph<ExprType>.add(rewrite: RewriteResult): EClassId = when (rewrite) {
    is RRLeaf -> rewrite.eClassId
    is RRBinary -> add(EBinary(add(rewrite.left), rewrite.operation.symbol, add(rewrite.right)))
}


tailrec fun saturate(eGraph: EGraph<Expr>, rewriteRules: List<RewriteRule>) {
    rewriteRules.forEach { rule ->
        val matches = eMatch(eGraph, rule.pattern)
        matches.forEach { match ->
            val rewritten = rule.rewrite(match) ?: return@forEach
            val rewriteEClass = eGraph.add(rewritten)
            eGraph.merge(match.eClassId, rewriteEClass)
        }
    }

    if (eGraph.worklist.isEmpty()) return

    eGraph.rebuild()

    saturate(eGraph, rewriteRules)
}
