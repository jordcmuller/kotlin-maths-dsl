package maths.core.rewriting

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
        is AnyNode -> RRLeaf(matchResult.matchedGroups[name]!!)
        is ConstMatcher -> RRConst(Const(value))
        is UnaryMatcher -> RRUnary(operation, operand.eMatcherToRewriteResult(matchResult))
        is BinaryMatcher -> RRBinary(
            left.eMatcherToRewriteResult(matchResult),
            operations.first(), // TODO: make this respect the operation of the matched expression if multiple operations can be matched
            right.eMatcherToRewriteResult(matchResult)
        )
        else -> error("Unknown template")
    }
}


class RewriteRule(
    val name: String,
    val pattern: EMatcher,
    val template: EMatcher,
    val rewrite: (EMatchResult) -> RewriteResult? = { template.eMatcherToRewriteResult(it) }
) {
    override fun toString() = "$name: $pattern -> $template"
}
