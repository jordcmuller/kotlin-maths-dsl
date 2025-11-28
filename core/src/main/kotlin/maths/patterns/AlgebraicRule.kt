package maths.patterns

import maths.core.ast.Operation

sealed interface AlgebraicRule {
    val name: String
}

object Commutative : AlgebraicRule { override val name = "Commutative" }
object Associative : AlgebraicRule { override val name = "Associative" }
// Future: object Distributive : AlgebraicRule { override val name = "Distributive" }

val propertyMap = mapOf(
    Operation.ADD to setOf(Commutative, Associative),
    Operation.MUL to setOf(Commutative, Associative),
    Operation.SUB to emptySet(),
    Operation.DIV to emptySet(),
)