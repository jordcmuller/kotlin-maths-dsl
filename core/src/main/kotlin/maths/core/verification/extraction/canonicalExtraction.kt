package maths.core.verification.extraction

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Operation
import maths.core.ast.UnaryExpr
import maths.core.ast.Var
import maths.core.dsl.maths
import maths.core.dsl.plus
import maths.core.verification.EBinary
import maths.core.verification.EClass
import maths.core.verification.EConst
import maths.core.verification.ENode
import maths.core.verification.EUnary
import maths.core.verification.EVar

val nodeDepth = hashMapOf<ENode, Int>()
const val maxDepth = 10

val EClass.canonicalForm get(): Expr = mostShallowENode.toExpr()

val EClass.mostShallowENode: ENode get() {
    require(nodes.isNotEmpty()) { "At least one node is required" }

    nodes.firstOrNull { it is EConst } ?.let { return it }
    nodes.firstOrNull { it is EVar } ?.let { return it }

    return nodes.minBy { it.depth }
}

val ENode.depth: Int get() = calculateDepth()

private fun ENode.calculateDepth(currentDepth: Int = 0): Int = nodeDepth.getOrPut(this) {
    if (currentDepth >= maxDepth) currentDepth
    else when (this) {
        is EConst -> 0
        is EVar -> 1
        is EUnary, is EBinary -> 1 + childEClasses.sumOf { it.mostShallowENode.calculateDepth(currentDepth + 1) }
    }
}

fun ENode.toExpr() = when (this) {
    is EConst -> Const(identifier.toDouble())
    is EVar -> Var(identifier)
    is EUnary -> UnaryExpr(Operation.fromString(operation), operand.canonicalForm)
    is EBinary -> BinaryExpr(left.canonicalForm, Operation.fromString(operation), right.canonicalForm)
}


fun main() {
    maths {
        val x by variable()
        val y by variable()

        x + 1 equal y

        println(state.eGraph.eClasses.map { it.canonicalForm })
    }
}