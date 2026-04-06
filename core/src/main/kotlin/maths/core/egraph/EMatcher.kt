package maths.core.egraph

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Operation
import maths.core.ast.UnaryExpr
import maths.core.ast.Var

sealed class EMatcher(val children: List<EMatcher> = emptyList()) {
    override fun toString(): String {
        return when (this) {
            is AnyNode -> name
            is ConstMatcher -> "$value"
            is VarMatcher -> "'$name'"
            is BinaryMatcher -> "($left ${operation.symbol} $right)"
            else -> TODO("toString not yet implemented: $this")
        }
    }
}

data class AnyNode(val name: String): EMatcher()
data class ConstMatcher(val value: Int) : EMatcher()
data class VarMatcher(val name: String) : EMatcher()
data class BinaryMatcher(val left: EMatcher, val operation: Operation, val right: EMatcher) : EMatcher(listOf(left, right))
data class UnaryMatcher(val operation: Operation, val operand: EMatcher) : EMatcher(listOf(operand))

class FunctionMatcher(val name: String, val parameterCount: Int, val body: EMatcher) : EMatcher(listOf(body))
class FunctionParameterMatcher(val operation: Operation) : EMatcher()

fun Expr.toEMatcher(): EMatcher {
    return when (this) {
        is Const -> ConstMatcher(value)
        is Var -> AnyNode(name)
        is UnaryExpr -> UnaryMatcher(operation, operand.toEMatcher())
        is BinaryExpr -> BinaryMatcher(left.toEMatcher(), operation, right.toEMatcher())
        else -> TODO("EMatcher mapping for $javaClass not supported yet")
    }
}

fun Expr.toExactEMatcher(): EMatcher = when (this) {
    is Const -> ConstMatcher(value)
    is Var -> VarMatcher(name)
    is UnaryExpr -> UnaryMatcher(operation, operand.toExactEMatcher())
    is BinaryExpr -> BinaryMatcher(left.toExactEMatcher(), operation, right.toExactEMatcher())
    else -> TODO("EMatcher mapping for $javaClass not supported yet")
}
