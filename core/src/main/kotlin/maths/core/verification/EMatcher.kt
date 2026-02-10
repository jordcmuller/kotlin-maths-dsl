package maths.core.verification

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Operation
import maths.core.ast.Operation.ADD
import maths.core.ast.Operation.DIV
import maths.core.ast.Operation.MUL
import maths.core.ast.Operation.SUB
import maths.core.ast.Var

sealed class EMatcher(val children: List<EMatcher> = emptyList()) {
    override fun toString(): String {
        return when (this) {
            is AnyNode -> name
            is ConstMatcher -> "$value"
            is VarMatcher -> "'$name'"
            is BinaryMatcher -> "($left ${operations.joinToString("/") { it.symbol }} $right)"
            else -> TODO("toString not yet implemented: $this")
        }
    }
}
object AnyVar: EMatcher()
object AnyConst: EMatcher()
class AnyNode(val name: String): EMatcher()
class ConstMatcher(val value: Double) : EMatcher()
class VarMatcher(val name: String) : EMatcher()
class BinaryMatcher(val left: EMatcher, val operation: Operation, val right: EMatcher) : EMatcher(listOf(left, right))
class UnaryMatcher(val operation: Operation, val operand: EMatcher) : EMatcher(listOf(operand))



operator fun EMatcher.plus(that: EMatcher) = BinaryMatcher(this, listOf(ADD), that)
operator fun EMatcher.minus(that: EMatcher) = BinaryMatcher(this, listOf(SUB), that)
operator fun EMatcher.times(that: EMatcher) = BinaryMatcher(this, listOf(MUL), that)
operator fun EMatcher.div(that: EMatcher) = BinaryMatcher(this, listOf(DIV), that)

operator fun EMatcher.plus(that: Number) = BinaryMatcher(this, listOf(ADD), ConstMatcher(that.toDouble()))
operator fun EMatcher.minus(that: Number) = BinaryMatcher(this, listOf(SUB), ConstMatcher(that.toDouble()))
operator fun EMatcher.times(that: Number) = BinaryMatcher(this, listOf(MUL), ConstMatcher(that.toDouble()))
operator fun EMatcher.div(that: Number) = BinaryMatcher(this, listOf(DIV), ConstMatcher(that.toDouble()))

operator fun Number.plus(that: EMatcher) = BinaryMatcher(ConstMatcher(this.toDouble()), listOf(ADD), that)
operator fun Number.minus(that: EMatcher) = BinaryMatcher(ConstMatcher(this.toDouble()), listOf(SUB), that)
operator fun Number.times(that: EMatcher) = BinaryMatcher(ConstMatcher(this.toDouble()), listOf(MUL), that)
operator fun Number.div(that: EMatcher) = BinaryMatcher(ConstMatcher(this.toDouble()), listOf(DIV), that)



sealed class EMatch(val eClass: EClass)
data class EMNamedLeaf(val id: EClass, val name: String) : EMatch(id)
//data class EMLeaf(val id: EClassId) : EMatch(id)
//data class EMBinary(val id: EClassId, val left: EMatch, val operation: Operation, val right: EMatch) : EMatch(id)

sealed interface RewriteResult
data class RRLeaf(val eClass: EClass) : RewriteResult
data class RRConst<ExprType: Expr>(val value: ExprType) : RewriteResult
data class RRUnary(val operation: Operation, val operand: RewriteResult) : RewriteResult
data class RRBinary(val left: RewriteResult, val operation: Operation, val right: RewriteResult) : RewriteResult

fun Expr.toEMatcher(): EMatcher {
    return when (this) {
        is Const -> ConstMatcher(value)
        is Var -> VarMatcher(name)
        is BinaryExpr -> BinaryMatcher(left.toEMatcher(), listOf(operation), right.toEMatcher())
        else -> TODO("EMatcher mapping for $javaClass not supported yet")
    }
}
