package maths.core.dsl

import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Equivalence
import maths.core.ast.Expr
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

    operator fun invoke(statementsBlock: MathsContext.() -> Unit) = statementsBlock()
}

fun maths(statementsBlock: MathsContext.() -> Unit): MathsContext {
    return MathsContext().apply(statementsBlock)
}
