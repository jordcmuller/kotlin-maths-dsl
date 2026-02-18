package maths.core.dsl

import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Limit
import maths.core.ast.Var

data class LimitDimension(val variable: Var, val approaches: Expr)

operator fun Var.rangeTo(value: Const): LimitDimension = LimitDimension(this, value)
operator fun Var.rangeTo(value: Number): LimitDimension = LimitDimension(this, value.c)

fun lim(dimension: LimitDimension, function: () -> Expr) = Limit(dimension.variable, dimension.approaches, function())