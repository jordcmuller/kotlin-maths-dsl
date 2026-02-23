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

            is Func -> {
                TODO("Scope the function parameters to the function name like f.x, f.y")
                // Since we are scoping the params, does it not make more sense
                // to create a new e-graph and have that act as a separate scope?
            }

            else -> error("Unknown expression")
        }
}

