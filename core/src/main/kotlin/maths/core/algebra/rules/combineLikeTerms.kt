package maths.core.algebra.rules

import maths.core.algebra.Rule
import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Mul
import maths.core.ast.Var


// combine like-terms for linear ax + bx -> (a+b)x (very naive)
val combineLikeTerms = Rule("combine-like-linear") { e ->
    // (a*x + b*x) -> ( (a+b) * x )
    if (e is Add) {
        val (l, r) = e.left to e.right
        if (l is Mul && r is Mul && l.right is Var && r.right is Var) {
            val lx = l.left;
            val rx = r.left
            if (lx is Const && rx is Const && l.right.name == r.right.name) {
                return@Rule Mul(Const(lx.value + rx.value), l.right)
            }
        }
    }
    null
}
