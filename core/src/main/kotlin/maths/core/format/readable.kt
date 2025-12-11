package maths.core.format

import maths.core.ast.Add
import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Div
import maths.core.ast.Equation
import maths.core.ast.Expr
import maths.core.ast.Func
import maths.core.ast.Mul
import maths.core.ast.Neg
import maths.core.ast.Pow
import maths.core.ast.Stmt
import maths.core.ast.Sub
import maths.core.ast.Var
import maths.core.ast.VariableDeclaration

// Human-readable math (written form)
fun Expr.readable(): String = when (this) {
    is Const -> {
        val intValue = value.toInt()
        if (value == intValue.toDouble()) intValue.toString() else value.toString()
    }
    is Var -> name
    is Add -> "${left.readable()} + ${right.readable()}"
    is Sub -> "${left.readable()} - ${right.readable()}"
    is Mul -> {
        // Drop the * for cleaner output, handle coeffs like 2x
        val leftStr = left.readable()
        val rightStr = right.readable()
        if (left is Const || right is Const) "$leftStr$rightStr" else "$leftStr·$rightStr"
    }
    is Div -> "${left.readable()} / ${right.readable()}"
    is BinaryExpr -> "${left.readable()} ${operation.symbol} ${right.readable()}"
    is Pow -> {
        val exp = exp.readable()
        "${base.readable()}${superscript(exp.toInt())}"
    }

    is Neg -> "-${child.readable()}"
    is Func -> TODO()
}

fun Stmt.readable(): String = when (this) {
    is Equation -> "${left.readable()} = ${right.readable()}"
    is VariableDeclaration -> "let ${this.variable.name}"
}

// Helper for superscript conversion (for readable mode)
fun superscript(n: Int): String = when (n) {
    1 -> "¹"
    2 -> "²"
    3 -> "³"
    4 -> "⁴"
    5 -> "⁵"
    6 -> "⁶"
    7 -> "⁷"
    8 -> "⁸"
    9 -> "⁹"
    else -> "^$n"
}
