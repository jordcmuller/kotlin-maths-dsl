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
