package maths.patterns

import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Operation

sealed interface AlgebraicRule {
    val name: String
}

object Commutative : AlgebraicRule { override val name = "Commutative" }
object Associative : AlgebraicRule { override val name = "Associative" }
class Identity(identityElement: Expr) : AlgebraicRule { override val name = "Associative" }
// Future: object Distributive : AlgebraicRule { override val name = "Distributive" }

val propertyMap = mapOf(
    Operation.ADD to setOf(Commutative, Associative, Identity(Const(0.0))),
    Operation.MUL to setOf(Commutative, Associative, Identity(Const(1.0))),
    Operation.SUB to setOf(Associative, Identity(Const(0.0))),
    Operation.DIV to setOf(Associative, Identity(Const(1.0))),
)