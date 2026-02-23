package maths.core.rewriting

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.UnaryExpr
import maths.core.dsl.maths
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.ConstMatcher
import maths.core.verification.EMatchResult
import maths.core.verification.EMatcher
import maths.core.verification.UnaryMatcher

fun EMatcher.eMatcherToExpr(matchResult: EMatchResult): Expr {
    return when (this) {
        is AnyNode -> matchResult[this] ?: error("$this is not matched to $matchResult")
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

data class GraphQuery(val premises: List<QueryCondition>)

abstract class RewriteRule(val name: String, val query: GraphQuery) {
    abstract val rewrite: (EMatchResult) -> Expr?
}

class TemplateRewriteRule (
    name: String,
    query: GraphQuery,
    val template: EMatcher,
): RewriteRule(name, query) {
    override val rewrite: (EMatchResult) -> Expr? = template::eMatcherToExpr
    override fun toString() = "$name: $query -> $template"
}
