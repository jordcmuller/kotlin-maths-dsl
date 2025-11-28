package maths.core.algebra.rules

import maths.core.algebra.Rule
import maths.core.ast.Add
import maths.core.ast.Const
import maths.core.ast.Div
import maths.core.ast.Mul
import maths.core.ast.Pow
import maths.core.ast.Sub
import kotlin.math.pow

val constantFolding = Rule("constant-fold") { e ->
    when (e) {
        is Add -> {
            if (e.left is Const && e.right is Const) Const(e.left.value + e.right.value) else null
        }

        is Sub -> {
            if (e.left is Const && e.right is Const) Const(e.left.value - e.right.value) else null
        }

        is Mul -> {
            if (e.left is Const && e.right is Const) Const(e.left.value * e.right.value) else null
        }

        is Div -> {
            if (e.left is Const && e.right is Const) {
                val denominator = e.right.value
                if (denominator == 0.0) null else Const(e.left.value / denominator)
            } else null
        }

        is Pow -> {
            if (e.base is Const && e.exp is Const) {
                val v = e.base.value.pow(e.exp.value)
                Const(v)
            } else null
        }

        else -> null
    }
}
