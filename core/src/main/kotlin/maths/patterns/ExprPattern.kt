package maths.patterns

import maths.core.ast.*

sealed class ExprPattern : Pattern<Expr> {
    companion object {
        fun variable(name: String? = null) = VariablePattern(name)
        fun constant(value: Double? = null) = ConstantPattern(value)
        fun binary(left: ExprPattern, op: Operation, right: ExprPattern) =
            BinaryPattern(left, op, right)

        fun fromExpr(expr: Expr): ExprPattern = when (expr) {
            is Var -> variable(expr.name)
            is Const -> constant(expr.value)
            is BinaryExpr -> binary(fromExpr(expr.left), expr.operation, fromExpr(expr.right))
            is Func -> TODO()
            is Neg -> TODO()
            is Pow -> TODO()
        }
    }
}

class VariablePattern(private val name: String?) : ExprPattern() {
    override fun accepts(value: Expr): Boolean =
        value is Var && (name == null || value.name == name)
}

class ConstantPattern(private val value: Double?) : ExprPattern() {
    override fun accepts(value: Expr): Boolean =
        value is Const && (this.value == null || value.value == this.value)
}

class BinaryPattern(
    val left: ExprPattern,
    val operation: Operation,
    val right: ExprPattern
) : ExprPattern() {
    override fun accepts(value: Expr): Boolean {
        if (value !is BinaryExpr || value.operation != operation) return false

        // Try the original structure first
        if (left.accepts(value.left) && right.accepts(value.right)) return true

        // Try each algebraic rule in turn
        val operationProperties = propertyMap[operation] ?: return false

        for (property in operationProperties) {
            val matcher = RuleRegistry.matcherFor(property)
            if (matcher != null && matcher.matches(this, value)) {
                return true
            }
        }

        return false
    }
}
