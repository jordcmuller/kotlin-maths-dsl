package maths.core

class EqualToExpression(val left: MObject, val right: MObject): CompositeExpression(listOf(left, right)) {
}