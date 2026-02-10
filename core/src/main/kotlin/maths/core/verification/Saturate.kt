package maths.core.verification

import maths.core.ast.Expr
import maths.core.rewriting.RewriteRule

fun <ExprType: Expr> EGraph<ExprType>.add(rewrite: RewriteResult): EClass = when (rewrite) {
    is RRLeaf -> rewrite.eClass
    is RRConst<*> -> lowerer.lower(rewrite.value as ExprType, ::add)
    is RRUnary -> add(EUnary(rewrite.operation.symbol, add(rewrite.operand)))
    is RRBinary -> add(EBinary(add(rewrite.left), rewrite.operation.symbol, add(rewrite.right)))
}

tailrec fun <ExprType: Expr> EGraph<ExprType>.saturate(rewriteRules: List<RewriteRule>, maxIterations: Int = 10) {
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
