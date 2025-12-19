package maths.core.format

import maths.core.ast.*

// LaTeX-friendly output
fun Expr.printKotlin(): String = when (this) {
    is Const -> {
        val intValue = value.toInt()
        if (value == intValue.toDouble()) intValue.toString() else value.toString()
    }
    is Var -> name
    is Add -> "${left.latex()} + ${right.latex()}"
    is Mul -> "${left.latex()} * ${right.latex()}"
    is Div -> "${left.latex()} / ${right.latex()}"
    is Sub -> TODO()
    is BinaryExpr -> TODO()
    is Pow -> "{${base.latex()}} pow {${exp.latex()}}" // what to do when squared?
    is Func -> TODO()
    is Neg -> TODO()
    else -> TODO()
}

fun Equation.printKotlin(): String = "${left.latex()} eq ${right.latex()}"
