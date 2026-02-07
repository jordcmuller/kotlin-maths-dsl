package maths.core.rewriting.dsl

import maths.core.verification.AnyNode
import kotlin.reflect.KProperty

class AnyNodeDelegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = AnyNode(property.name)
}

fun anyNode() = AnyNodeDelegate()
