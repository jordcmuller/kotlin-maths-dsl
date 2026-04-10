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
import maths.core.rewriting.multiplicativeAssociativity
import maths.core.rewriting.multiplicativeCancellation
import maths.core.rewriting.multiplicativeCommutativity
import maths.core.rewriting.multiplicativeIdentity
import maths.core.rewriting.multiplicativeInverse
import maths.core.egraph.MathsEGraph
import maths.core.egraph.analysis.OperationRegistry
import maths.core.egraph.analysis.OperatorRegistry
import maths.core.egraph.saturate
import maths.core.rewriting.squared

@DslMarker
annotation class MathsDsl

@MathsDsl
class MathsContext(noRules: Boolean = false) {

    val rewriteRules = mutableListOf<RewriteRule>()
    init { if (!noRules) withAdditionAndMultiplicationRules() }

    val operatorRegistry = OperatorRegistry()
    val eGraph = MathsEGraph()

    init { eGraph.analysis = operatorRegistry }

    val statements = mutableListOf<Stmt>()
    val errors = mutableListOf<ValidationError>()

    /** DSL Definition */
    infix fun Expr.equal(other: Expr) = (this eq other).equivalence == Equivalence.True
    infix fun Expr.notEqual(other: Expr) = (this eq other).equivalence == Equivalence.False

    infix fun Expr.eq(other: Expr): Equation = Equation(this, other).also(::processEquation)
    infix fun Expr.eq(other: Int) = this eq Const(other)

    fun variable(name: String? = null) = VariableDelegate(name)

    infix fun Expr.equate(other: Expr) {
        val leftEClass = eGraph.add(this)
        val rightEClass = eGraph.add(other)
        eGraph.queueMergeAndRebuild(leftEClass, rightEClass)
    }

    infix fun Number.equate(other: Expr) = this.c equate other
    infix fun Expr.equate(other: Number) = this equate other.c

    infix fun Number.equal(other: Expr) = this.c equal other
    infix fun Expr.equal(other: Number) = this equal other.c

    operator fun invoke(statementsBlock: MathsContext.() -> Unit) = statementsBlock()

    fun withRule(rewriteRuleFunc: () -> RewriteRule) {
        rewriteRules += rewriteRuleFunc()
    }

    inline fun <reified T1: Any, reified T2: Any> withOperation(symbol: String, noinline func: (T1, T2) -> Any) {
        val operator = operatorRegistry.map.getOrPut(symbol) { OperationRegistry(symbol) }
        operator.register(func)
    }

    companion object {
        val empty = MathsContext(noRules = true)
    }

    fun processEquation(equation: Equation) {
        val equivalence = checkEquivalence(equation.left, equation.right)
        if (equivalence == Equivalence.False) errors.add(ValidationError(equation, "Equation is not true"))

        equation.equivalence = equivalence
    }

    private fun checkEquivalence(left: Expr, right: Expr): Equivalence {
        if (semanticallyEquivalent(left, right)) {
            return Equivalence.True
        }

        return Equivalence.False
    }

    private fun semanticallyEquivalent(a: Expr, b: Expr): Boolean {
        val aEClass = eGraph.add(a)
        val bEClass = eGraph.add(b)

        if (aEClass == bEClass) return true

        eGraph.saturate(rewriteRules)

        return eGraph.findCanonicalEClass(aEClass) == eGraph.findCanonicalEClass(bEClass)
    }
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
        squared,
        multiplicativeCancellation
    )
}
