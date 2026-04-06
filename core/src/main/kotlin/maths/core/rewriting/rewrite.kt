package maths.core.rewriting

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Equation
import maths.core.ast.Expr
import maths.core.ast.Var
import maths.core.dsl.MathsContext
import maths.core.state.processEquation
import maths.patterns.ExprPattern

class RewriteContext(var expression: Expr) {
    context(mathsContext: MathsContext) infix fun Expr.with(replacement: Expr) {
        expression = rewrite(expression, this, replacement)
    }
}

context(_: MathsContext)
infix fun Expr.rewrite(rewriteBlock: RewriteContext.() -> Unit): Expr {
    return RewriteContext(this).apply(rewriteBlock).expression
}

context(_: MathsContext)
infix fun Expr.rewrite(equation: Equation): Expr {
    // confirm that the rewrite rule is valid in the context
    // require(equation.equivalence == Equivalence.True) { "Rewrite rule is not valid in the given context" }

    // replace the sub expression with the new term and return the updated original expression
    return rewrite(this, equation.left, equation.right)
}

context(mathsContext: MathsContext)
fun rewrite(initial: Expr, subExpression: Expr, replacement: Expr): Expr {
    Equation(subExpression, replacement).also {
        mathsContext.processEquation(it)
        if (it.equivalence == False) error("Invalid expression rewrite in the context")
    }
    return ExpressionRewritingVisitor(subExpression, replacement).rewrite(initial)
}

operator fun Expr.contains(subExpression: Expr) = findSubExpression(this, subExpression) != null

fun findSubExpression(expression: Expr, subExpression: Expr): Expr? {
    if (ExprPattern.fromExpr(subExpression).accepts(expression)) return expression

    return when (expression) {
        is Const, is Var -> null
        is BinaryExpr -> findSubExpression(expression.left, subExpression) ?: findSubExpression(expression.right, subExpression)
        else -> expression
    }
}

class ExpressionRewritingVisitor(val subExpression: Expr, val replacement: Expr) {
    var replaced = false

    fun rewrite(expression: Expr): Expr {
        val output = visit(expression)
        if (!replaced) error("Sub expression not found")
        return output
    }

    fun visit(expression: Expr): Expr {
        if (ExprPattern.fromExpr(subExpression).accepts(expression)) {
            replaced = true
            return replacement
        }

        return when (expression) {
            is BinaryExpr -> BinaryExpr(visit(expression.left), expression.operation, visit(expression.right))
            is Const, is Var -> expression
            else -> expression
        }
    }
}