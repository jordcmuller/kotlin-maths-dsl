package maths.core.verification

import maths.core.ast.*

class ExprLowerer {
    fun lower(expr: Expr, add: (ENode) -> EClass): EClass =
        when (expr) {
            is Const -> add(EConst(expr.value))

            is Var -> add(EVar(expr.name))

            is BinaryExpr -> {
                val l = lower(expr.left, add)
                val r = lower(expr.right, add)
                add(EBinary(l, expr.operation.symbol, r))
            }

            is UnaryExpr -> {
                val operand = lower(expr.operand, add)
                add(EUnary(expr.operation.symbol, operand))
            }

            else -> error("Unknown expression")
        }
}

