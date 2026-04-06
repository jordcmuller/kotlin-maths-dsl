package maths.patterns

import maths.core.ast.*

sealed class ExprPattern : Pattern<Expr> {
    companion object {
        private fun variable(name: String) = VariablePattern(name)
        private fun constant(value: Int) = ConstantPattern(value)
        private fun binary(left: ExprPattern, op: Operation, right: ExprPattern) =
            BinaryPattern(left, op, right)

        fun fromExpr(expr: Expr): ExprPattern = when (expr) {
            zero -> ZeroPattern()
            is Successor -> SuccessorPattern(fromExpr(expr.of))
            is Var -> variable(expr.name)
            is Const -> constant(expr.value)
            is BinaryExpr -> binary(fromExpr(expr.left), expr.operation, fromExpr(expr.right))
            is Func -> TODO()
            is Neg -> TODO()
            else -> TODO("else branch")
        }
    }
}

class ZeroPattern : ExprPattern() {
    override fun accepts(value: Expr): Boolean = value == zero
}

class SuccessorPattern(private val of: ExprPattern) : ExprPattern() {
    override fun accepts(value: Expr): Boolean = value is Successor && of.accepts(value.of)
}

class VariablePattern(private val name: String) : ExprPattern() {
    override fun accepts(value: Expr): Boolean =
        value is Var && value.name == name
}

class ConstantPattern(private val value: Int) : ExprPattern() {
    override fun accepts(value: Expr): Boolean =
        value is Const && value.value == this.value
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

        return false
    }
}


/*
    TODO: Move this to the maths context
     instead of baking the property identity into the accept method,
     extend the equivalence by creating the other instances of the expression
     and connecting these to the original expression with the equivalence classes in the context.
     This should be more scalable as it treats the expressions themselves as data and provides a single
     point to extend from in a generic manner. Any equivalence identities will be valid here.
     This could be properties like
        associativity: a + b + c = a + (b + c)
        commutativity: a + b = b + a
        distributivity: a * (b + c) = a * b + a * c
        identity: a + 0 = a, a * 1 = a

         // Try each algebraic rule in turn
        val operationProperties = propertyMap[operation] ?: return false

        for (property in operationProperties) {
            val matcher = RuleRegistry.matcherFor(property)
            if (matcher != null && matcher.matches(this, value)) {
                return true
            }
        }


* */