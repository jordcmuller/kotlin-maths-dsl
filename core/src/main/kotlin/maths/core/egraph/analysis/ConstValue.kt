package maths.core.egraph.analysis

import java.math.BigDecimal
import java.math.BigInteger

sealed interface ConstValue : JoinSemiLattice<ConstValue> {

    data object Unknown : ConstValue {
        override fun join(other: ConstValue): ConstValue = other
    }

    /**
     * Exact integer.
     */
    data class IntVal(val value: Int) : ConstValue {
        override fun join(other: ConstValue): ConstValue =
            when (other) {
                Unknown -> this
                is IntVal -> if (value == other.value) this else Conflict
                is RationalVal -> {
                    val asRat = Rational(value.toBigInteger(), 1.toBigInteger())
                    if (asRat == other.value) other else Conflict
                }
                is RealVal -> {
                    // Only safe if RealVal is exact semantics.
                    if (other.value.compareTo(value.toBigDecimal()) == 0) other else Conflict
                }
                Conflict -> Conflict
            }

        override fun toString(): String {
            return value.toString()
        }
    }

    /**
     * Exact rational (p/q in lowest terms, q > 0).
     */
    data class RationalVal(val value: Rational) : ConstValue {
        override fun join(other: ConstValue): ConstValue =
            when (other) {
                Unknown -> this
                is IntVal -> other.join(this) // delegate to IntVal logic
                is RationalVal -> if (value == other.value) this else Conflict
                is RealVal -> {
                    // rational can embed into real
                    if (other.value.compareTo(value.toBigDecimalExact()) == 0) other else Conflict
                }
                Conflict -> Conflict
            }
    }

    /**
     * Exact real. If you mean floating-point approximation, DO NOT do equality joins like this.
     */
    data class RealVal(val value: BigDecimal) : ConstValue {
        override fun join(other: ConstValue): ConstValue =
            when (other) {
                Unknown -> this
                is IntVal -> other.join(this)
                is RationalVal -> other.join(this)
                is RealVal -> if (value.compareTo(other.value) == 0) this else Conflict
                Conflict -> Conflict
            }
    }

    /**
     * Represents "this e-class cannot consistently have a single constant value".
     */
    data object Conflict : ConstValue {
        override fun join(other: ConstValue): ConstValue = Conflict
    }
}