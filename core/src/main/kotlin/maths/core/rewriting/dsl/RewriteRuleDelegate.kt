package maths.core.rewriting.dsl

import maths.core.rewriting.TemplateRewriteRule
import maths.core.verification.EMatcher
import kotlin.reflect.KProperty

class RewriteRuleDelegate(name: String, pattern: EMatcher, template: EMatcher) {
    // TODO: confirm that the template has all the available captured groups
    val rule = TemplateRewriteRule(name, pattern, template)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = rule
}

operator fun Pair<EMatcher, EMatcher>.provideDelegate(thisRef: Any?, property: KProperty<*>) = RewriteRuleDelegate(property.name, first, second)
