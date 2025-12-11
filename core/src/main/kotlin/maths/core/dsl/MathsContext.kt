package maths.core.dsl

import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Div
import maths.core.ast.Equation
import maths.core.ast.Expr
import maths.core.ast.Mul
import maths.core.ast.Neg
import maths.core.ast.Pow
import maths.core.ast.Sub
import maths.core.ast.Var
import maths.core.ast.VariableDeclaration
import maths.core.state.MathsState
import maths.core.state.processEquation
import maths.core.state.processVariableDeclaration
import kotlin.reflect.KProperty

@DslMarker
annotation class MathsDsl

@MathsDsl
class MathsContext {
    val state = MathsState()

    /** DSL Definition */
    val Int.c get() = Const(this.toDouble())

    infix fun Expr.eq(other: Expr): Equation = Equation(this, other).also(state::processEquation)
    infix fun Expr.eq(other: Int) = this eq Const(other.toDouble())

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

    // Variable delegate
    inner class VariableDelegate(private val name: String? = null) {
        lateinit var variableDeclaration: VariableDeclaration

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Var {
            return variableDeclaration.variable
        }

        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): VariableDelegate {
            variableDeclaration = VariableDeclaration(name ?: property.name)
            state.processVariableDeclaration(variableDeclaration)
            return this
        }
    }

    fun variable(name: String? = null) = VariableDelegate(name)

    val String.v get() = Var(this)

    fun printErrors() {
        println(state.errors.joinToString("\n"))
    }
}

fun maths(statementsBlock: MathsContext.() -> Unit): MathsContext {
    return MathsContext().apply(statementsBlock)
}
