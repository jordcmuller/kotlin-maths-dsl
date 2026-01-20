package maths.core.dsl

import maths.core.ast.*

operator fun Double.plus(e: Expr) = Add(Const(this), e)
operator fun Double.times(e: Expr) = Mul(Const(this), e)
operator fun Int.plus(e: Expr) = Add(Const(this.toDouble()), e)
operator fun Int.times(e: Expr) = Mul(Const(this.toDouble()), e)
operator fun Expr.plus(other: Expr) = Add(this, other)
operator fun Expr.minus(other: Expr) = Sub(this, other)
operator fun Expr.times(other: Expr) = Mul(this, other)
operator fun Expr.div(other: Expr) = Div(this, other)
operator fun Expr.unaryMinus() = Neg(this)
operator fun Expr.plus(other: Int) = Add(this, Const(other.toDouble()))
operator fun Expr.minus(other: Int) = Sub(this, Const(other.toDouble()))
operator fun Expr.times(other: Int) = Mul(this, Const(other.toDouble()))
operator fun Expr.div(other: Int) = Div(this, Const(other.toDouble()))
fun Expr.pow(e: Expr) = Pow(this, e)
fun Expr.pow(n: Int) = Pow(this, Const(n.toDouble()))
val Expr.squared get() = Pow(this, Const(2.toDouble()))

val Int.c get() = Const(this.toDouble())

val String.v get() = Var(this)
