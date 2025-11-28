package maths.patterns

import maths.core.ast.*

object CommutativeMatcher : RuleMatcher {
    override fun matches(pattern: ExprPattern.BinaryPattern, value: BinaryExpr): Boolean {
        return (pattern.left.accepts(value.left) && pattern.right.accepts(value.right)) ||
               (pattern.left.accepts(value.right) && pattern.right.accepts(value.left))
    }
}

object AssociativeMatcher : RuleMatcher {
    override fun matches(pattern: ExprPattern.BinaryPattern, value: BinaryExpr): Boolean {
        val patternSet = flattenPattern(pattern, value.operation)
        val exprSet = flattenExpr(value, value.operation)
        return exprSet.size == patternSet.size &&
               exprSet.zip(patternSet).all { (e, p) -> p.accepts(e) }
    }

    private fun flattenExpr(expr: Expr, op: Operation): List<Expr> =
        when (expr) {
            is BinaryExpr if (expr.operation == op) -> flattenExpr(expr.left, op) + flattenExpr(expr.right, op)
            else -> listOf(expr)
        }

    private fun flattenPattern(pattern: ExprPattern, op: Operation): List<ExprPattern> =
        when (pattern) {
            is ExprPattern.BinaryPattern if (pattern.operation == op) ->
                flattenPattern(pattern.left, op) + flattenPattern(pattern.right, op)
            else -> listOf(pattern)
        }
}
