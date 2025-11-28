package maths.core.algebra.rules

import maths.core.algebra.Rule
import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Mul
import maths.core.ast.Pow
import maths.core.ast.Var


// perfect square factor detection (x^2 + 2*x + 1) -> (x+1)^2
val perfectSquare = Rule("perfect-square") { e ->
    if (e is Add) {
        // naive pattern for (x^2 + 2*x + 1)
        val terms = flattenAdd(e)
        // match pattern
        if (terms.size == 3) {
            val (a, b, c) = terms
            if (a is Pow && a.exp is Const && a.exp.value == 2.0
                && b is Mul && b.left is Const && b.left.value == 2.0
                && b.right is Var && a.base is Var && a.base.name == b.right.name
                && c is Const && (c.value == 1.0)
            ) {
                val x = a.base
                return@Rule Pow(Add(x, Const(1.0)), Const(2.0))
            }
        }
    }
    null
}

fun flattenAdd(e: Expr): List<Expr> {
    return when (e) {
        is Add -> flattenAdd(e.left) + flattenAdd(e.right)
        else -> listOf(e)
    }
}
