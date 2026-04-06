package maths.core.egraph.analysis

import java.math.BigDecimal
import java.math.BigInteger

data class Rational(
    val num: BigInteger,
    val den: BigInteger
) {
    init {
        require(den != BigInteger.ZERO)
    }

    companion object {
        fun of(num: BigInteger, den: BigInteger): Rational {
            require(den != BigInteger.ZERO)
            var n = num
            var d = den
            if (d.signum() < 0) {
                n = n.negate()
                d = d.negate()
            }
            val g = n.gcd(d)
            return Rational(n / g, d / g)
        }
    }

    fun toBigDecimalExact(): BigDecimal {
        // WARNING: exact conversion may be impossible if den has factors other than 2 and 5.
        // You may instead choose to disallow conversion or store RealVal as rational+sqrt/etc.
        return BigDecimal(num).divide(BigDecimal(den))
    }
}