package maths.core.rewriting.dsl

import maths.core.ast.Expr
import maths.core.egraph.query.ConditionFactory
import maths.core.egraph.query.GraphQuery
import maths.core.egraph.query.QueryCondition
import maths.core.egraph.query.query
import maths.core.rewriting.RewriteRule
import kotlin.reflect.KProperty

class RewriteRuleBuilder {
    var name = ""
    lateinit var leftHandSide: Expr
    lateinit var rightHandSide: Expr
    lateinit var conditionBlock: ConditionFactory.() -> QueryCondition
    var implies: Boolean = false

    fun build(): RewriteRule {
        val queries = mutableListOf<GraphQuery>()
        queries += query {
            match { leftHandSide }
            if (::conditionBlock.isInitialized) {
                where(conditionBlock)
            }
            then {
                leftHandSide equate rightHandSide
            }
        }

        if (!implies) queries += query {
            match { rightHandSide }
            if (::conditionBlock.isInitialized) {
                where(conditionBlock)
            }
            then {
                rightHandSide equate leftHandSide
            }
        }

        return RewriteRule(name, queries)
    }
}

class RewriteRuleDelegate(val builder: RewriteRuleBuilder) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): RewriteRule {
        return builder.apply { name = property.name }.build()
    }
}

operator fun Pair<Expr, Expr>.provideDelegate(thisRef: Any?, property: KProperty<*>): RewriteRuleDelegate {
    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        leftHandSide = first
        rightHandSide = second
    }
    return RewriteRuleDelegate(rewriteRuleBuilder)
}

class ImpliesPair(val leftHandSide: Expr, val rightHandSide: Expr) {}

infix fun Expr.implies(other: Expr) = ImpliesPair(this, other)
infix fun Expr.impliedBy(other: Expr) = ImpliesPair(other, this)

operator fun ImpliesPair.provideDelegate(thisRef: Any?, property: KProperty<*>): RewriteRuleDelegate {
    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        implies = true
        leftHandSide = this@provideDelegate.leftHandSide
        rightHandSide = this@provideDelegate.rightHandSide
    }
    return RewriteRuleDelegate(rewriteRuleBuilder)
}

infix fun Pair<Expr, Expr>.where(conditionBlock: ConditionFactory.() -> QueryCondition): RewriteRuleDelegate {
    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        leftHandSide = first
        rightHandSide = second
        this.conditionBlock = conditionBlock
    }

    return RewriteRuleDelegate(rewriteRuleBuilder)
}

fun rewriteRule(name: String = "", ruleBlock: () -> Pair<Expr, Expr>): RewriteRule {
    val pair = ruleBlock()

    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        this.name = name
        leftHandSide = pair.first
        rightHandSide = pair.second
    }

    return rewriteRuleBuilder.build()
}

operator fun String.invoke(ruleBlock: () -> Pair<Expr, Expr>) = rewriteRule(this, ruleBlock)
