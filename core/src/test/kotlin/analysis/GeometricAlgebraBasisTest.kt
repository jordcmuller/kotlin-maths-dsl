package analysis

import io.kotest.core.spec.style.StringSpec
import maths.core.ast.BinaryExpr
import maths.core.ast.Expr
import maths.core.ast.Mul
import maths.core.dsl.MathsContext
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.egraph.BaseENode
import maths.core.egraph.EBinary
import maths.core.egraph.EClass
import maths.core.egraph.EConst
import kotlin.collections.groupBy

open class Blade(val dims: List<String>) {
    override fun toString() = dims.joinToString("")
    operator fun times(other: Blade) = Blade(dims + other.dims)
}

fun multiplyBlades(a: Blade, b: Blade): Any {
    val remainingDims = (a.dims + b.dims)
        .groupBy { it }
        .filter { (_ = key, dims = value) ->
            dims.map { true }.fold(false) { isPresent, next -> isPresent xor next }
        }.keys.toList()

    if (remainingDims.isEmpty()) return 1

    return Blade(remainingDims)
}

class BasisExpr(val name: String): Expr, EConst(Blade(listOf(name)))

class BasisMap {
    val bases = mutableListOf<BasisExpr>()

    operator fun get(i1: Int): Expr {
        require(i1 != 0) { "Zero indexes are not supported" }
        for (i in bases.size + 1..i1) bases.add(BasisExpr("e$i"))
        return bases[i1-1]
    }
    operator fun get(vararg ints: Int) = ints
        .map { get(it) }
        .reduce { a, b -> Mul(a, b) }
}

val e = BasisMap()

class GeometricAlgebraTest : StringSpec({
    "Basic basis vector operations" {
        MathsContext.empty {
            withENode { basis: BasisExpr -> basis }
            withOperation("*", ::multiplyBlades)

            val xHat by variable()
            val yHat by variable()

            xHat equate e[1]
            yHat equate e[2]

            require(e[1] * e[1] equal 1.c)

            val v by variable()

            v equate 1.c + e[1] + e[2] + e[1, 2]

            require(v equal e[1] + e[1, 2] + 1.c + e[2])
        }
    }
})