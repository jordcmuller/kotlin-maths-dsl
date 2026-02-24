package maths.core.dsl

import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Equivalence
import maths.core.ast.Expr
import maths.core.ast.Stmt
import maths.core.format.readable
import maths.core.rewriting.RewriteRule
import maths.core.rewriting.additiveAssociativity
import maths.core.rewriting.additiveCommutativity
import maths.core.rewriting.additiveIdentity
import maths.core.rewriting.additiveInverse
import maths.core.rewriting.distributivity
import maths.core.rewriting.dsl.RewriteRuleBuilder
import maths.core.rewriting.multiplicativeAssociativity
import maths.core.rewriting.multiplicativeCancellation
import maths.core.rewriting.multiplicativeCommutativity
import maths.core.rewriting.multiplicativeIdentity
import maths.core.rewriting.multiplicativeInverse
import maths.core.state.processEquation
import maths.core.egraph.MathsEGraph

@DslMarker
annotation class MathsDsl

@MathsDsl
class MathsContext(noRules: Boolean = false) {

    init { if (!noRules) withAdditionAndMultiplicationRules() }

    val eGraph = MathsEGraph()
    val rewriteRules = mutableListOf<RewriteRule>()

    val statements = mutableListOf<Stmt>()
    val errors = mutableListOf<ValidationError>()

    /** DSL Definition */
    infix fun Expr.equal(other: Expr) = (this eq other).equivalence == Equivalence.True
    infix fun Expr.notEqual(other: Expr) = (this eq other).equivalence == Equivalence.False

    infix fun Expr.eq(other: Expr): Equation = Equation(this, other).also(::processEquation)
    infix fun Expr.eq(other: Int) = this eq Const(other.toDouble())

    fun variable(name: String? = null) = VariableDelegate(name).also { eGraph.add(it.variable) }

    infix fun Expr.equate(other: Expr) {
        val leftEClass = eGraph.add(this)
        val rightEClass = eGraph.add(other)
        eGraph.mergeAndRebuild(leftEClass, rightEClass)
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

/** A simple error container */
class ValidationError(val statement: Stmt, val message: String) {
    override fun toString(): String {
        return "'${statement.readable()}' is invalid due to '$message'"
    }
}

fun MathsContext.withAdditionRules() {
    rewriteRules += listOf(
        additiveCommutativity,
        additiveAssociativity,
        additiveIdentity,
        additiveInverse,
    )
}

fun MathsContext.withMultiplicationRules() {
    rewriteRules += listOf(
        multiplicativeCommutativity,
        multiplicativeAssociativity,
        multiplicativeIdentity,
        multiplicativeInverse,
    )
}

fun MathsContext.withAdditionAndMultiplicationRules() {
    withAdditionRules()
    withMultiplicationRules()
    rewriteRules += listOf(
        distributivity,
        multiplicativeCancellation
    )
}
