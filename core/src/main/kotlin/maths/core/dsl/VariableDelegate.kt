package maths.core.dsl

import maths.core.ast.Var
import kotlin.reflect.KProperty

class VariableDelegate(private val name: String? = null) {
    lateinit var variable: Var

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = variable

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): VariableDelegate {
        variable = Var(name ?: property.name)
        return this
    }
}

fun variable(name: String? = null) = VariableDelegate(name)
