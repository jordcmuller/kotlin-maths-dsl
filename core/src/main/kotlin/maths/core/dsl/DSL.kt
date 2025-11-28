package maths.core.dsl

import maths.core.ast.Add
import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Div
import maths.core.ast.Equation
import maths.core.ast.Expr
import maths.core.ast.Mul
import maths.core.ast.Neg
import maths.core.ast.Operation
import maths.core.ast.Pow
import maths.core.ast.Stmt
import maths.core.ast.Sub
import maths.core.ast.Var
import maths.core.ast.VariableDeclaration
import maths.core.format.readable
import maths.core.verification.AddEqualityResult
import maths.core.verification.CongruenceClosure
import maths.patterns.ExprPattern
import kotlin.reflect.KProperty

// ---------------------------
// DSL helpers
// ---------------------------

enum class Equivalence {
    Unknown,
    True,
    False,
}

@DslMarker
annotation class MathsDsl

@MathsDsl
class MathsContext {
    /** DSL Definition */

    // Variable delegate
    inner class VariableDelegate(private val name: String? = null) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Var {
            return Var(name ?: property.name)
        }

        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): VariableDelegate {
            validateStatement(VariableDeclaration(property.name))
            return VariableDelegate()
        }
    }

    fun variable(name: String? = null) = VariableDelegate(name)

    infix fun Expr.eq(other: Expr): Equation = Equation(this, other).also { validateStatement(it) }
    infix fun Expr.eq(other: Int) = Equation(this, Const(other.toDouble())).also { validateStatement(it) }

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

    /** Context Methods */
    // TODO: add things here to help process each of the mathematical statements in order

    fun outputErrors(): String {
        return errors.joinToString("\n")
    }

    private val representations = mutableMapOf<String, MutableList<Expr>>()
    private val variableValues = mutableMapOf<String, Expr>()
    private val history = mutableListOf<Stmt>()

    private val definedVars = mutableSetOf<String>()
    val errors = mutableListOf<ValidationError>()

    val congruenceClosure = CongruenceClosure()

    fun isDeclared(name: String) = definedVars.contains(name)

    /** if the expression is a variable then we resolve whatever it is attached to
     * if the variable is not linked to any other expressions or values then null is returned
     * if the expression is not a variable then we return the expression
     * */

    fun getBindings(variable: Var): MutableSet<Expr> {
        return mutableSetOf<Expr>().also { resolveBindingsInContext(variable, it) }
    }

    fun resolveBindingsInContext(variable: Var, bindingSet: MutableSet<Expr>) {
        val bindings = representations[variable.name] ?: return
        for (binding in bindings) {
            bindingSet.add(binding)
            if (binding is Var) resolveBindingsInContext(binding, bindingSet)
        }
    }

    fun isKnown(variable: Var) = variableValues.containsKey(variable.name)
    fun isUnknown(variable: Var) = !isKnown(variable)

    fun bind(name: String, expr: Expr) {
        // TODO: update this to include all expressions not just variables
        representations.getOrPut(name) { mutableListOf() }.add(expr)
        if (expr is Const) { variableValues[name] = expr }
    }

    fun validateStatement(stmt: Stmt): List<ValidationError> {
        history += stmt

        return when (stmt) {
            is VariableDeclaration -> processVariableDeclaration(stmt)
            is Equation -> processEquation(stmt)
        }.also { errors.addAll(it) }
    }
    
    fun processVariableDeclaration(variable: VariableDeclaration): List<ValidationError> {
        val statementErrors = mutableListOf<ValidationError>()
        if (isDeclared(variable.name)) {
            statementErrors.add(ValidationError(variable, "Variable '${variable.name}' is already defined"))
        } else {
            declareVar(variable.name)
        }
        return statementErrors
    }

    fun processEquation(equation: Equation): List<ValidationError> {
        val statementErrors = mutableListOf<ValidationError>()
        
        // TODO: what are the conditions to check equivalence vs not

        val left = equation.left
        val right = equation.right

        if (!semanticallyEquivalent(left, right)) {
            statementErrors.add(
                ValidationError(
                    equation,
                    "Equality conflicts: ${equation.left.readable()} != ${equation.right.readable()}"
                )
            )
            return statementErrors
        }

        if (left is Var && isUnknown(left)) {
            bind(left.name, right)
        }

        if (right is Var && isUnknown(right)) {
            bind(right.name, left)
        }

        return statementErrors
    }
    
//    fun isComputable(expr: Expr): Boolean {
//        return true // but should actually check if all the children in an expression are computable
//        TODO("How to check computability?")
//    }

//    fun computationallyEquivalent(left: Expr, right: Expr): Boolean {
//        TODO("add some kind of computability here")
//    }

//    private fun checkEquivalence(left: Expr, right: Expr): Equivalence {
//        // TODO: which situations result in unknown equivalence
//
//        if (semanticallyEquivalent(left, right)) {
//            return Equivalence.True
//        }
//
//        if (isComputable(left) && isComputable(right)) {
//            if (computationallyEquivalent(left, right)) {
//                return Equivalence.True
//            }
//        }
//
//        return Equivalence.False
//    }

    private fun semanticallyEquivalent(a: Expr, b: Expr): Boolean {
        val leftRepresentations = if (a is Var) getBindings(a).apply { add(a) } else setOf(a)
        val rightRepresentations = if (b is Var) getBindings(b).apply { add(b) } else setOf(b)

        val result = congruenceClosure.addEquality(a, b)
        
        return result !is AddEqualityResult.Conflict
//        for (leftRepresentation in leftRepresentations) {
//            for (rightRepresentation in rightRepresentations) {
//                val semanticallyEquivalent = ExprPattern.fromExpr(leftRepresentation).accepts(rightRepresentation)
//                if (semanticallyEquivalent) return true
//            }
//        }

//        return false
    }

//    private fun evaluateExpr(expr: Expr): Double? {
//        // Very naive evaluator — real one would handle variables with context
//        return when (expr) {
//            is Const -> expr.value
//            is BinaryExpr -> {
//                val l = evaluateExpr(expr.left)
//                val r = evaluateExpr(expr.right)
//                if (l != null && r != null) {
//                    when (expr.operation) {
//                        Operation.ADD -> l + r
//                        Operation.SUB -> l - r
//                        Operation.MUL -> l * r
//                        Operation.DIV -> if (r != 0.0) l / r else null
//                    }
//                } else null
//            }
//
//            else -> null // Variables or unknown
//        }
//    }

    /** Called when a new variable is declared */
    fun declareVar(name: String) {
        definedVars += name
    }
}

/** A simple error container */
class ValidationError(val statement: Stmt, val message: String) {
    override fun toString(): String {
        return "'${statement.readable()}' is invalid due to '$message')"
    }
}

fun maths(statements: MathsContext.() -> Unit) {
    MathsContext().apply(statements).also { println(it.outputErrors()) }
}