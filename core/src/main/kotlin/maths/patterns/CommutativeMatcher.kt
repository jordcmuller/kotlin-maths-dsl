package maths.patterns

import maths.core.ast.*

object CommutativeMatcher : RuleMatcher {
    override fun matches(pattern: BinaryPattern, value: BinaryExpr): Boolean {
        return pattern.left.accepts(value.right) && pattern.right.accepts(value.left)
    }
}

object AssociativeMatcher : RuleMatcher {
    override fun matches(pattern: BinaryPattern, value: BinaryExpr): Boolean {
        if (pattern.left is BinaryPattern) {
            val associativePattern = BinaryPattern(
                pattern.left.left,
                pattern.left.operation,
                BinaryPattern(
                    pattern.left.right,
                    pattern.operation,
                    pattern.right
                )
            )

            return associativePattern.accepts(value)
        }

        if (pattern.right is BinaryPattern) {
            val associativePattern = BinaryPattern(
                BinaryPattern(
                    pattern.left,
                    pattern.operation,
                    pattern.right.left
                ),
                pattern.right.operation,
                pattern.right.right
            )
            return associativePattern.accepts(value)
        }

        return false
    }
}

object IdentityMatcher : RuleMatcher {
    val identityElement: Expr = Const(0.0)

    override fun matches(pattern: BinaryPattern, value: BinaryExpr): Boolean {
        // if x + 0
        // then match only left

        // if 0 + x
        // then match only right

        if (pattern.left is ConstantPattern && pattern.left.accepts(identityElement)) {
            return pattern.right.accepts(value)
        }

        if (pattern.right is ConstantPattern && pattern.right.accepts(identityElement)) {
            return pattern.left.accepts(value)
        }

        return false
    }
}
