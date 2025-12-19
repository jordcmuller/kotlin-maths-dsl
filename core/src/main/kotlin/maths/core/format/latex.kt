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
import maths.core.ast.Sub
import maths.core.ast.Var

// LaTeX-friendly output
fun Expr.latex(): String = when (this) {
    is Const -> {
        val intValue = value.toInt()
        if (value == intValue.toDouble()) intValue.toString() else value.toString()
    }
    is Var -> name
    is Add -> "${left.latex()} + ${right.latex()}"
    is Mul -> "${left.latex()} ${right.latex()}"
    is Pow -> "{${base.latex()}}^{${exp.latex()}}"
    is Div -> TODO()
    is Sub -> TODO()
    is BinaryExpr -> TODO()
    is Func -> TODO()
    is Neg -> TODO()
    else -> TODO("else branch")
}

fun Equation.latex(): String = "${left.latex()} = ${right.latex()}"
