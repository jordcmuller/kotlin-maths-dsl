package maths.core.compare

import maths.core.algebra.AlgebraRegistry
import maths.core.ast.Expr
import maths.core.algebra.simplify

// ---------------------------
// Expression equivalence (very naive canonicalization)
// ---------------------------
fun canonicalize(e: Expr): Expr {
    // for MVP we run simplifier with registry rules
    val (out, _) = simplify(e, AlgebraRegistry.listRules())
    return out
}

fun expressionsEquivalent(a: Expr, b: Expr): Boolean {
    val ca = canonicalize(a)
    val cb = canonicalize(b)
    // naive structural compare
    return ca == cb
}
