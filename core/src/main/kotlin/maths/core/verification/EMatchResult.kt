package maths.core.verification

import maths.core.ast.Expr

class EMatchResult(val matchedExpressions: Map<String, Expr> = emptyMap()) {
    operator fun get(anyNode: AnyNode) = matchedExpressions[anyNode.name]
}
