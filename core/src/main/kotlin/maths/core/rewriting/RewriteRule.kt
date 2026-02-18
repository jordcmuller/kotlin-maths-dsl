package maths.core.rewriting

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.UnaryExpr
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.ConstMatcher
import maths.core.verification.EMatchResult
import maths.core.verification.EMatcher
import maths.core.verification.UnaryMatcher

private fun EMatcher.eMatcherToExpr(matchResult: EMatchResult): Expr {
    return when (this) {
        is AnyNode -> matchResult[this]
        is ConstMatcher -> Const(value)
        is UnaryMatcher -> UnaryExpr(operation, operand.eMatcherToExpr(matchResult))
        is BinaryMatcher -> BinaryExpr(
            left.eMatcherToExpr(matchResult),
            operation,
            right.eMatcherToExpr(matchResult)
        )
        else -> error("Unknown template $this")
    }
}

abstract class RewriteRule(
    val name: String,
    val pattern: EMatcher,
) {
    abstract val rewrite: (EMatchResult) -> Expr?
}

class TemplateRewriteRule (
    name: String,
    pattern: EMatcher,
    val template: EMatcher,
): RewriteRule(name, pattern) {
    override val rewrite: (EMatchResult) -> Expr? = { template.eMatcherToExpr(it) }
    override fun toString() = "$name: $pattern -> $template"
}
