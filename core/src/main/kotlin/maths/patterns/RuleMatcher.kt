package maths.patterns

import maths.core.ast.BinaryExpr

fun interface RuleMatcher {
    fun matches(pattern: BinaryPattern, value: BinaryExpr): Boolean
}
