package maths.core.rewriting

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.UnaryExpr
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.egraph.AnyNode
import maths.core.egraph.BinaryMatcher
import maths.core.egraph.ConstMatcher
import maths.core.egraph.EMatchResult
import maths.core.egraph.EMatcher
import maths.core.egraph.UnaryMatcher
import maths.core.egraph.action.GraphAction
import maths.core.egraph.action.GraphActionBuilder
import maths.core.egraph.query.GraphQuery
import maths.core.egraph.query.query

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

open class RewriteRule(val name: String, val query: GraphQuery, val actions: List<GraphAction>) {
    override fun toString(): String {
        return name
    }
}

infix fun GraphQuery.then(actionBuilderFunc: GraphActionBuilder.() -> Unit): RewriteRule {
    val actions = GraphActionBuilder().apply(actionBuilderFunc).build()
    return RewriteRule("empty name", this, actions)
}

fun main() {
    val x by variable()
    val y by variable()

    val rule = query {
        match { x + y }
        where { x equal y }
    } then {
        produce { y + x }
    }
}