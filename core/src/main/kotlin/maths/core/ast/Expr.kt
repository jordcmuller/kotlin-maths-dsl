package maths.core.ast

import maths.patterns.ExprPattern
import kotlin.reflect.KProperty

interface Expr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Var {
        return Var(property.name) // improve on this
    }

    infix fun matches(expr: Expr) = ExprPattern.fromExpr(this).accepts(expr)
}