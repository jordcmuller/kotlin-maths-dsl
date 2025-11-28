package maths.core

interface MExpression: MObject {
}

sealed class LeafExpression: MExpression {}
sealed class CompositeExpression(val children: List<MObject>): MExpression {}