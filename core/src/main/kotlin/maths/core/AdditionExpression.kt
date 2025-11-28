package maths.core

class AdditionExpression(val left: MObject, val right: MObject): CompositeExpression(listOf(left, right)) {
}
