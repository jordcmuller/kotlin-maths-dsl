package maths.core.rewriting.dsl

import maths.core.ast.Expr
import maths.core.egraph.action.actions
import maths.core.egraph.query.ConditionFactory
import maths.core.egraph.query.QueryCondition
import maths.core.egraph.query.query
import maths.core.rewriting.RewriteRule
import kotlin.reflect.KProperty

class RewriteRuleBuilder {
    var name = ""
    lateinit var from: Expr
    lateinit var to: Expr
    lateinit var conditionBlock: ConditionFactory.() -> QueryCondition

    fun build(): RewriteRule {
        val query = query {
            match { from }
            if (::conditionBlock.isInitialized) {
                where(conditionBlock)
            }
        }

        val actions = actions {
            from equate to
        }

        return RewriteRule(name, query, actions)
    }
}

class RewriteRuleDelegate(val builder: RewriteRuleBuilder) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): RewriteRule {
        return builder.apply { name = property.name }.build()
    }
}

operator fun Pair<Expr, Expr>.provideDelegate(thisRef: Any?, property: KProperty<*>): RewriteRuleDelegate {
    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        from = first
        to = second
    }
    return RewriteRuleDelegate(rewriteRuleBuilder)
}

infix fun Pair<Expr, Expr>.where(conditionBlock: ConditionFactory.() -> QueryCondition): RewriteRuleDelegate {
    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        from = first
        to = second
        this.conditionBlock = conditionBlock
    }

    return RewriteRuleDelegate(rewriteRuleBuilder)
}

fun rewriteRule(name: String = "", ruleBlock: () -> Pair<Expr, Expr>): RewriteRule {
    val pair = ruleBlock()

    val rewriteRuleBuilder = RewriteRuleBuilder().apply {
        this.name = name
        from = pair.first
        to = pair.second
    }

    return rewriteRuleBuilder.build()
}

operator fun String.invoke(ruleBlock: () -> Pair<Expr, Expr>) = rewriteRule(this, ruleBlock)
