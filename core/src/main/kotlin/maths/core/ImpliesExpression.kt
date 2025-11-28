package maths.core

class ImpliesExpression(val left: MObject, val right: MObject): CompositeExpression(listOf(left, right)) {
}