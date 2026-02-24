package maths.core.rewriting.dsl

import maths.core.ast.Expr
import maths.core.egraph.querying.ConditionFactory
import maths.core.egraph.querying.GraphQuery
import maths.core.egraph.querying.PatternCondition
import maths.core.egraph.querying.QueryCondition
import maths.core.rewriting.TemplateRewriteRule
import maths.core.egraph.EMatcher
import maths.core.egraph.toEMatcher
import kotlin.reflect.KProperty

class RewriteRuleBuilder(pattern: EMatcher, val template: EMatcher) {
    val premises: MutableList<QueryCondition> = mutableListOf(PatternCondition(pattern))

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = TemplateRewriteRule(property.name, GraphQuery(premises), template)
}

operator fun Pair<Expr, Expr>.provideDelegate(thisRef: Any?, property: KProperty<*>) =
    RewriteRuleBuilder(first.toEMatcher(), second.toEMatcher())

fun rewriteRule(ruleBlock: () -> Pair<Expr, Expr>) = ruleBlock().let {
    RewriteRuleBuilder(it.first.toEMatcher(), it.second.toEMatcher())
}

infix fun RewriteRuleBuilder.where(conditionBlock: ConditionFactory.() -> QueryCondition) = apply {
    premises.add(ConditionFactory.conditionBlock())
}

object ConditionFactory {
    infix fun Expr.equal(other: Expr) = EqualsCondition(PatternCondition(this.toEMatcher()), PatternCondition(other.toEMatcher()))
    infix fun Expr.notEqual(other: Expr) = NotEqualsCondition(PatternCondition(this.toEMatcher()), PatternCondition(other.toEMatcher()))
    infix fun QueryCondition.and(other: QueryCondition) = AndCondition(this, other)
    infix fun QueryCondition.or(other: QueryCondition) = OrCondition(this, other)
//    operator fun QueryCondition.not() = NotCondition(this)
}
