package maths.core.dsl

import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Equivalence
import maths.core.ast.Expr
import maths.core.state.MathsState
import maths.core.state.processEquation

@DslMarker
annotation class MathsDsl

@MathsDsl
class MathsContext {
    val state = MathsState()

    /** DSL Definition */
    infix fun Expr.equal(other: Expr) = (this eq other).equivalence == Equivalence.True
    infix fun Expr.notEqual(other: Expr) = (this eq other).equivalence == Equivalence.False

    infix fun Expr.eq(other: Expr): Equation = Equation(this, other).also(state::processEquation)
    infix fun Expr.eq(other: Int) = this eq Const(other.toDouble())

    fun variable(name: String? = null) = VariableDelegate(name)

    infix fun Expr.equate(other: Expr) {
        val leftEClass = state.eGraph.add(this)
        val rightEClass = state.eGraph.add(other)
        state.eGraph.mergeAndRebuild(leftEClass, rightEClass)
    }

    infix fun Number.equate(other: Expr) = this.c equate other
    infix fun Expr.equate(other: Number) = this equate other.c

    infix fun Number.equal(other: Expr) = this.c equal other
    infix fun Expr.equal(other: Number) = this equal other.c

    operator fun invoke(statementsBlock: MathsContext.() -> Unit) = statementsBlock()
}

fun maths(statementsBlock: MathsContext.() -> Unit): MathsContext {
    return MathsContext().apply(statementsBlock)
}
