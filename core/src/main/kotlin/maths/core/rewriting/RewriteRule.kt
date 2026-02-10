package maths.core.rewriting

import maths.core.ast.Const
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.ConstMatcher
import maths.core.verification.EMatchResult
import maths.core.verification.EMatcher
import maths.core.verification.RRBinary
import maths.core.verification.RRConst
import maths.core.verification.RRLeaf
import maths.core.verification.RRUnary
import maths.core.verification.RewriteResult
import maths.core.verification.UnaryMatcher

private fun EMatcher.eMatcherToRewriteResult(matchResult: EMatchResult): RewriteResult {
    return when (this) {
        is AnyNode -> RRLeaf(matchResult[this])
        is ConstMatcher -> RRConst(Const(value))
        is UnaryMatcher -> RRUnary(operation, operand.eMatcherToRewriteResult(matchResult))
        is BinaryMatcher -> RRBinary(
            left.eMatcherToRewriteResult(matchResult),
            operation,
            right.eMatcherToRewriteResult(matchResult)
        )
        else -> error("Unknown template $this")
    }
}

abstract class RewriteRule(
    val name: String,
    val pattern: EMatcher,
) {
    abstract val rewrite: (EMatchResult) -> RewriteResult?
}

class TemplateRewriteRule (
    name: String,
    pattern: EMatcher,
    val template: EMatcher,
): RewriteRule(name, pattern) {
    override val rewrite: (EMatchResult) -> RewriteResult? = { template.eMatcherToRewriteResult(it) }
    override fun toString() = "$name: $pattern -> $template"
}
