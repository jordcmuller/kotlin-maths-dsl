package maths.core.algebra

import maths.core.format.raw
import maths.core.algebra.rules.combineLikeTerms
import maths.core.algebra.rules.constantFolding
import maths.core.algebra.rules.perfectSquare
import maths.core.algebra.rules.simplifyNeg
import maths.core.ast.Add
import maths.core.ast.Div
import maths.core.ast.Expr
import maths.core.ast.Func
import maths.core.ast.Mul
import maths.core.ast.Neg
import maths.core.ast.Pow
import maths.core.ast.Sub

// ---------------------------
// Rule + Derivation step
// ---------------------------
data class Step(val ruleName: String, val before: Expr, val after: Expr, val explanation: String? = null) {
    fun pretty() = "${ruleName}: ${before.raw()}  ->  ${after.raw()}${explanation?.let { " // $it" } ?: ""}"
}

typealias RuleFn = (Expr) -> Expr?

data class Rule(val name: String, val apply: RuleFn)

// ---------------------------
// Deriver / Simplifier
// ---------------------------
class Derivation {
    val steps = mutableListOf<Step>()
    fun record(step: Step) = steps.add(step)
    fun printTrace() {
        println("Derivation trace:")
        steps.forEachIndexed { i, s -> println("${i + 1}. ${s.pretty()}") }
    }
}


/**
 * Apply rules repeatedly using a simple breadth-first traversal until no rule applies,
 * returning the resulting expression and a derivation trace.
 */
fun simplify(expr: Expr, rules: List<Rule>, maxSteps: Int = 100): Pair<Expr, Derivation> {
    val deriv = Derivation()
    var current = expr
    var changed = true
    var steps = 0
    while (changed && steps < maxSteps) {
        changed = false
        val candidate = applyOnce(current, rules, deriv)
        if (candidate != null) {
            deriv.record(Step("apply", current, candidate, "applied one rewrite"))
            current = candidate
            changed = true
            steps++
        }
    }
    return current to deriv
}

/**
 * Try apply each rule to each node (pre-order), returning first changed expression.
 */
fun applyOnce(expr: Expr, rules: List<Rule>, derivation: Derivation): Expr? {
    // try node itself
    for (r in rules) {
        val out = r.apply(expr)
        if (out != null) {
            derivation.record(Step(r.name, expr, out))
            return out
        }
    }
    // recurse
    return when (expr) {
        is Add -> {
            val l = applyOnce(expr.left, rules, derivation)
            if (l != null) return Add(l, expr.right)
            val r = applyOnce(expr.right, rules, derivation)
            if (r != null) return Add(expr.left, r)
            null
        }

        is Sub -> {
            val l = applyOnce(expr.left, rules, derivation)
            if (l != null) return Sub(l, expr.right)
            val r = applyOnce(expr.right, rules, derivation)
            if (r != null) return Sub(expr.left, r)
            null
        }

        is Mul -> {
            val l = applyOnce(expr.left, rules, derivation)
            if (l != null) return Mul(l, expr.right)
            val r = applyOnce(expr.right, rules, derivation)
            if (r != null) return Mul(expr.left, r)
            null
        }

        is Div -> {
            val l = applyOnce(expr.left, rules, derivation)
            if (l != null) return Div(l, expr.right)
            val r = applyOnce(expr.right, rules, derivation)
            if (r != null) return Div(expr.left, r)
            null
        }

        is Pow -> {
            val b = applyOnce(expr.base, rules, derivation)
            if (b != null) return Pow(b, expr.exp)
            val e = applyOnce(expr.exp, rules, derivation)
            if (e != null) return Pow(expr.base, e)
            null
        }

        is Neg -> {
            val c = applyOnce(expr.child, rules, derivation)
            if (c != null) return Neg(c)
            null
        }

        is Func -> {
            val a = applyOnce(expr.arg, rules, derivation)
            if (a != null) return Func(expr.name, a)
            null
        }

        else -> null
    }
}

fun installDefaultRules() {
    AlgebraRegistry.registerRule(constantFolding)
    AlgebraRegistry.registerRule(simplifyNeg)
    AlgebraRegistry.registerRule(combineLikeTerms)
    AlgebraRegistry.registerRule(perfectSquare)
}

