package maths.core.egraph.query

import maths.core.ast.Expr
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.egraph.toEMatcher

data class GraphQuery(val premises: List<QueryCondition>)

class QueryBuilder {
    private val premises = mutableListOf<QueryCondition>()

    fun match(exprFunc: () -> Expr) {
        premises += PatternCondition(exprFunc().toEMatcher())
    }

    fun where(conditionFunc: ConditionFactory.() -> QueryCondition) {
        premises += ConditionFactory.conditionFunc()
    }

    fun build(): GraphQuery = GraphQuery(premises)
}

object ConditionFactory {
    infix fun Expr.equal(other: Expr) = EqualsCondition(PatternCondition(this.toEMatcher()), PatternCondition(other.toEMatcher()))
    infix fun Expr.notEqual(other: Expr) = NotEqualsCondition(PatternCondition(this.toEMatcher()), PatternCondition(other.toEMatcher()))
    infix fun QueryCondition.and(other: QueryCondition) = AndCondition(this, other)
    infix fun QueryCondition.or(other: QueryCondition) = OrCondition(this, other)
//    operator fun QueryCondition.not() = NotCondition(this)
}

fun query(queryBuilderFunc: QueryBuilder.() -> Unit): GraphQuery {
    return QueryBuilder().apply(queryBuilderFunc).build()
}

fun main() {
    val x by variable()
    val y by variable()
    query {
        match { x + y }
        where { x equal y }
    }
}