package maths.core.verification

import maths.core.ast.*

interface ExprBuilder<ExprType> {
//    fun build(matches: List<EMatch>) = matches.map { build(it) }
    fun build(eNode: ENode, children: List<Expr> = emptyList()): ExprType
}

class MathsBuilder : ExprBuilder<Expr> {
    override fun build(eNode: ENode, children: List<Expr>): Expr {
        return when (eNode) {
            is EConst -> Const(eNode.value)
            is EVar -> Var(eNode.name)
            is EBinary ->
                BinaryExpr(
                    children[0],
                    getOperation(eNode.operation),
                    children[1]
                )
            else -> error("Unexpected evaluation operation")
        }
    }
}

fun getOperation(identifier: String) = Operation.entries.find { it.symbol == identifier }!!
