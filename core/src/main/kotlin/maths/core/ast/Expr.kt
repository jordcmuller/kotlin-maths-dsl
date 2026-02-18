package maths.core.ast

import maths.patterns.ExprPattern

interface Expr {
    infix fun matches(expr: Expr) = ExprPattern.fromExpr(this).accepts(expr)
}