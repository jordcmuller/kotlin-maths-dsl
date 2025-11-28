package maths.core.algebra.rules

import maths.core.algebra.Rule
import maths.core.ast.Const
import maths.core.ast.Neg

val simplifyNeg = Rule("neg-simplify") { e ->
    if (e is Neg && e.child is Const) {
        Const(-e.child.value)
    } else null
}
