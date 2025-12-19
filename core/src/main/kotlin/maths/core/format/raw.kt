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

// Extension functions for different output formats
fun Expr.raw(): String = when (this) {
    is Const -> value.toString()
    is Var -> name
    is Add -> "(${left.raw()} + ${right.raw()})"
    is Sub -> "(${left.raw()} - ${right.raw()})"
    is Mul -> "(${left.raw()} * ${right.raw()})"
    is Pow -> "(${base.raw()}^${exp.raw()})"
    is Div -> "(${left.raw()} / ${right.raw()})"
    is BinaryExpr -> TODO()
    is Neg -> "(-${child.raw()})"
    is Func -> TODO()
    else -> TODO("else branch")
}

fun Equation.raw(): String = "${left.raw()} = ${right.raw()}"
