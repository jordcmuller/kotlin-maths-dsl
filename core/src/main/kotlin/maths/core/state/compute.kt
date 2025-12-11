package maths.core.state

import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Var

fun compute(expr: Expr): Expr {
    return when (expr) {
        is Const -> expr
        is Var -> if (expr.value != null) compute(expr.value!!) else expr
        is Add -> add(expr.left, expr.right)
        else -> expr
    }
}

fun add(left: Expr, right: Expr) = when {
    left is Const && right is Const -> Const(left.value + right.value)
    else -> Add(left, right)
}