package maths.core.dsl

import maths.core.ast.*

operator fun Number.plus(e: Expr) = Add(Const(this.toDouble()), e)
operator fun Number.minus(e: Expr) = Sub(Const(this.toDouble()), e)
operator fun Number.times(e: Expr) = Mul(Const(this.toDouble()), e)
operator fun Number.div(e: Expr) = Div(Const(this.toDouble()), e)
operator fun Number.unaryMinus() = Neg(Const(this.toDouble()))
operator fun Expr.plus(other: Expr) = Add(this, other)
operator fun Expr.minus(other: Expr) = Sub(this, other)
operator fun Expr.times(other: Expr) = Mul(this, other)
operator fun Expr.div(other: Expr) = Div(this, other)
operator fun Expr.unaryMinus() = Neg(this)
operator fun Expr.plus(other: Number) = Add(this, Const(other.toDouble()))
operator fun Expr.minus(other: Number) = Sub(this, Const(other.toDouble()))
operator fun Expr.times(other: Number) = Mul(this, Const(other.toDouble()))
operator fun Expr.div(other: Number) = Div(this, Const(other.toDouble()))
fun Expr.pow(e: Expr) = Pow(this, e)
fun Expr.pow(n: Number) = Pow(this, Const(n.toDouble()))
val Expr.squared get() = Pow(this, Const(2.toDouble()))

val Number.c get() = Const(this.toDouble())

val String.v get() = Var(this)

operator fun Func.invoke(): Expr {
    require(params.isEmpty()) { "Only functions with no parameters can be invoked with no arguments." }
    return functionBody
}

operator fun Func.invoke(vararg args: Expr): Expr {
    require(params.size == args.size) { "Function call requires ${params.size} arguments but ${args.size} were given." }

    val bindings = params.map { it.name }.zip(args).toMap()

    return createFunctionEvaluation(functionBody, bindings)
}

operator fun Func.invoke(vararg func: Func): Func {
    require(params.size == func.size) { "Function call requires ${params.size} arguments but ${func.size} were given." }

    val bindings = params.map { it.name }.zip(func).toMap()
    val newFunctionBody = createFunctionEvaluation(functionBody, bindings)

    return Func(
        "$name of ${func.joinToString(", ") { it.name } }",
        func.flatMap { it.params },
        newFunctionBody
    )
}

private fun createFunctionEvaluation(functionExpr: Expr, parameterBindings: Map<String, Expr>): Expr {
    return when (functionExpr) {
        is Var -> {
            val value = parameterBindings[functionExpr.name] ?: error("Variable does not exist: ${functionExpr.name}")
            if (value is Func) value.functionBody
            else value
        }
        is Const -> Const(functionExpr.value)
        is UnaryExpr -> UnaryExpr(functionExpr.operation, createFunctionEvaluation(functionExpr.operand, parameterBindings))
        is BinaryExpr -> BinaryExpr(
            createFunctionEvaluation(functionExpr.left, parameterBindings),
            functionExpr.operation,
            createFunctionEvaluation(functionExpr.right, parameterBindings)
        )
        else -> error("Not yet implemented for ${functionExpr.javaClass}")
    }
}

infix fun Func.of(other: Func) = this(other)
