package maths.core.ast

import kotlin.reflect.KProperty

interface Expr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Var {
        return Var(property.name) // improve on this
    }
}