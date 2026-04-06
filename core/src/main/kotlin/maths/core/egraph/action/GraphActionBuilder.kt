package maths.core.egraph.action

import maths.core.ast.Expr
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.egraph.EGraph
import maths.core.egraph.EMatchResult
import maths.core.egraph.EMatcher
import maths.core.egraph.toEMatcher
import maths.core.rewriting.eMatcherToExpr

interface GraphAction {
    fun act(result: EMatchResult, graph: EGraph)
}

// add
class ProduceAction(val expression: EMatcher) : GraphAction {
    override fun act(result: EMatchResult, graph: EGraph) {
        graph.add(expression.eMatcherToExpr(result))
    }
}


// merge
class EquateAction(val left: EMatcher, val right: EMatcher) : GraphAction {
    override fun act(result: EMatchResult, graph: EGraph) {
        val leftId = graph.add(left.eMatcherToExpr(result))
        val rightId = graph.add(right.eMatcherToExpr(result))
        graph.queueMerge(leftId, rightId)
    }
}


class GraphActionBuilder {
    val actions = mutableListOf<GraphAction>()

    fun build(): List<GraphAction> {
        return actions
    }

    fun produce(expressionFunc: () -> Expr) {
        actions.add(ProduceAction(expressionFunc().toEMatcher()))
    }

    infix fun Expr.equate(other: Expr) {
        actions.add(EquateAction(this.toEMatcher(), other.toEMatcher()))
    }
}

fun actions(actionBuilderFunc: GraphActionBuilder.() -> Unit): List<GraphAction> {
    return GraphActionBuilder().apply(actionBuilderFunc).build()
}



fun main() {
    val x by variable()
    val y by variable()

    actions {
        produce { y + x }
        x + y equate y + x
    }
}