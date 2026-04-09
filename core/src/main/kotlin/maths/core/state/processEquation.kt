package maths.core.state

import maths.core.ast.Equation
import maths.core.ast.Equivalence
import maths.core.ast.Expr
import maths.core.dsl.MathsContext
import maths.core.dsl.ValidationError
import maths.core.egraph.saturate

fun MathsContext.processEquation(equation: Equation) {
    equation.equivalence = checkEquivalence(equation.left, equation.right)

    if (equation.equivalence == Equivalence.False) errors.add(ValidationError(equation, "Equation is not true"))
//    if (equation.equivalence == Equivalence.True) TODO("Update congruence closure")
}

fun computationallyEquivalent(left: Expr, right: Expr): Boolean {
    return compute(left) == compute(right)
}

private fun MathsContext.checkEquivalence(left: Expr, right: Expr): Equivalence {
    // TODO: which situations result in unknown equivalence

    if (semanticallyEquivalent(left, right)) {
        return Equivalence.True
    }

    if (computationallyEquivalent(left, right)) {
        return Equivalence.True
    }

    return Equivalence.False
}

private fun MathsContext.semanticallyEquivalent(a: Expr, b: Expr): Boolean {
    val aEClass = eGraph.add(a)
    val bEClass = eGraph.add(b)

    if (aEClass == bEClass) return true

    eGraph.saturate(rewriteRules)
    return eGraph.findEClass(aEClass) == eGraph.findEClass(bEClass)
}

//    private fun semanticallyEquivalent(a: Expr, b: Expr): Boolean {
//        val leftRepresentations = if (a is Var) getBindings(a).apply { add(a) } else setOf(a)
//        val rightRepresentations = if (b is Var) getBindings(b).apply { add(b) } else setOf(b)
//
//        val result = congruenceClosure.addEquality(a, b)
//
//        return result !is AddEqualityResult.Conflict
//        for (leftRepresentation in leftRepresentations) {
//            for (rightRepresentation in rightRepresentations) {
//                val semanticallyEquivalent = ExprPattern.fromExpr(leftRepresentation).accepts(rightRepresentation)
//                if (semanticallyEquivalent) return true
//            }
//        }

//        return false
//    }

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
