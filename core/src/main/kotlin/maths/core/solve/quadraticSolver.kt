package maths.core.solve

import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Expr
import maths.core.ast.Mul
import maths.core.ast.Pow
import maths.core.ast.Sub
import maths.core.ast.Var
import kotlin.math.sqrt


// ---------------------------
// Simple numeric solver for quadratic (MVP)
// ---------------------------
data class SolveResult(val solutions: List<Double>, val reason: String? = null)

fun solveQuadratic(eq: Equation): SolveResult? {
    // normalize to left - right = 0
    fun collectCoeffs(expr: Expr, X: String = "x"): Triple<Double, Double, Double> {
        // expects ax^2 + bx + c
        var a = 0.0;
        var b = 0.0;
        var c = 0.0
        fun walk(e: Expr, sign: Double = 1.0) {
            when (e) {
                is Add -> {
                    walk(e.left, sign); walk(e.right, sign)
                }

                is Sub -> {
                    walk(e.left, sign); walk(e.right, -sign)
                }

                is Mul -> {
                    if (e.left is Const && e.right is Pow && e.right.base is Var && (e.right.exp is Const) && e.right.exp.value == 2.0) {
                        a += sign * e.left.value
                    } else if (e.left is Const && e.right is Var) {
                        b += sign * e.left.value
                    } else if (e.left is Const && e.right is Const) {
                        c += sign * e.left.value
                    } else {
                        // ignore
                    }
                }

                is Pow -> {
                    if (e.base is Var && e.exp is Const && e.exp.value == 2.0) a += sign
                }

                is Var -> b += sign
                is Const -> c += sign * e.value
                else -> {}
            }
        }
        walk(expr)
        return Triple(a, b, c)
    }

    val norm = eq.normalized()
    val (a, b, c) = collectCoeffs(norm)
    if (a == 0.0 && b == 0.0) return null
    if (a == 0.0) {
        return SolveResult(listOf(-c / b), "Linear")
    }
    val disc = b * b - 4 * a * c
    return if (disc < 0) SolveResult(emptyList(), "No real solutions") else {
        val r1 = (-b + sqrt(disc)) / (2 * a)
        val r2 = (-b - sqrt(disc)) / (2 * a)
        SolveResult(listOf(r1, r2))
    }
}
