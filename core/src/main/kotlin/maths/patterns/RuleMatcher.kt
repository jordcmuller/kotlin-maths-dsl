package maths.patterns

import maths.core.ast.BinaryExpr

fun interface RuleMatcher {
    fun matches(pattern: ExprPattern.BinaryPattern, value: BinaryExpr): Boolean
}
