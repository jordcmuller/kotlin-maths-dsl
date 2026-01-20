package maths.core.format

import maths.core.ast.*

// kotlin syntax output
fun Expr.printKotlin(): String = when (this) {
    is Const -> {
        val intValue = value.toInt()
        if (value == intValue.toDouble()) intValue.toString() else value.toString()
    }
    is Var -> name
    is Add -> "${left.printKotlin()} + ${right.printKotlin()}"
    is Mul -> "${left.printKotlin()} * ${right.printKotlin()}"
    is Div -> "${left.printKotlin()} / ${right.printKotlin()}"
    is Sub -> TODO()
    is Pow -> "{${base.printKotlin()}} pow {${exp.printKotlin()}}" // what to do when squared?
    is BinaryExpr -> TODO()
    is Func -> TODO()
    is Neg -> TODO()
    else -> TODO()
}

fun Equation.printKotlin(): String = "${left.printKotlin()} eq ${right.printKotlin()}"
