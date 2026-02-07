package maths.core.verification

import maths.core.ast.*

interface ExprLowerer<E> {
    fun lower(expr: E, add: (ENode) -> EClass): EClass
}

class MathsLowerer : ExprLowerer<Expr> {
    override fun lower(expr: Expr, add: (ENode) -> EClass): EClass =
        when (expr) {
            is Const -> add(EConst(expr.value))

            is Var -> add(EVar(expr.name))

            is BinaryExpr -> {
                val operator = when (expr.operation) {
                    ADD -> "+"
                    SUB -> "-"
                    MUL -> "*"
                    DIV -> "/"
                    else -> error("Unknown operator $expr")
                }

                val l = lower(expr.left, add)
                val r = lower(expr.right, add)
                add(EBinary(l, operator, r))
            }

            else -> error("Unknown expression")
        }
}

