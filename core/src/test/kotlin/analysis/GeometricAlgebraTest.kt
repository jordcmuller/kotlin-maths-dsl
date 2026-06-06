//package analysis
//
//import io.kotest.core.spec.style.StringSpec
//import maths.core.ast.BinaryExpr
//import maths.core.ast.Expr
//import maths.core.dsl.MathsContext
//import maths.core.dsl.plus
//import maths.core.egraph.BaseENode
//import maths.core.egraph.EBinary
//import maths.core.egraph.EClass
//import kotlin.collections.groupBy
//
//open class MultiVector(vararg val components: VectorComponent) {
//    constructor(vararg multiVector: MultiVector) : this(*multiVector.toList().flatMap { it.components.toList() }
//        .toTypedArray())
//
//    constructor(vararg blade: Blade) : this(*blade.toList().map { VectorComponent(1, it) }.toTypedArray())
//
//    operator fun times(other: MultiVector) = geometricProduct(this, other)
//
//    override fun toString(): String {
//        return components.joinToString(separator = ", ", prefix = "<", postfix = ">")
//    }
//}
//
//fun multiVector(vararg components: Pair<Int, Blade>): Expr =
//    MultiVectorExpr(components.map { (coeff = first, blade = second) ->
//        CoefficientExpr(coeff) to BladeExpr(blade.dims)
//    })
//
//data class VectorComponent(val coeff: Int, val blade: Blade) {
//    operator fun times(component: VectorComponent): VectorComponent {
//        val newBlade = multiplyBlades(blade, component.blade)
//        val newCoeff = coeff * component.coeff
//        return VectorComponent(newCoeff, newBlade)
//    }
//
//    override fun toString() = "$coeff$blade"
//}
//
//open class Blade(val dims: List<String>) {
//    override fun toString() = dims.joinToString("")
//    operator fun times(other: Blade) = Blade(dims + other.dims)
//}
//
//data object x : Blade(listOf("x"))
//data object y : Blade(listOf("y"))
//data object z : Blade(listOf("z"))
//
//val xy = x * y
//
//operator fun Int.times(multiVector: MultiVector): MultiVector {
//    return multiVector.components
//        .map { VectorComponent(this * it.coeff, it.blade) }
//        .let { MultiVector(*it.toTypedArray()) }
//}
//
//fun multiplyBlades(a: Blade, b: Blade): Blade {
//    val remainingDims = (a.dims + b.dims)
//        .groupBy { it }
//        .filter { (key, dims = value) ->
//            dims.map { true }.fold(false) { isPresent, next -> isPresent xor next }
//        }.keys.toList()
//
//    return Blade(remainingDims)
//}
//
//fun normalise(components: List<VectorComponent>): List<VectorComponent> {
//    return components
//        .groupBy { it.blade }
//        .map { (dimension = key, components = value) ->
//            val newVal = components.fold(0) { acc, component -> acc + component.coeff }
//            VectorComponent(newVal, dimension)
//        }
//}
//
//fun add(a: MultiVector, b: MultiVector): MultiVector {
//    val summedComponents = normalise(a.components.toList() + b.components.toList())
//    return MultiVector(*summedComponents.toTypedArray())
//}
//
//fun geometricProduct(a: MultiVector, b: MultiVector): MultiVector {
//    val newComponents = mutableListOf<VectorComponent>()
//
//    a.components.forEach { ai ->
//        b.components.forEach { bi ->
//            newComponents += ai * bi
//        }
//    }
//
//    val normalised = normalise(newComponents)
//    return MultiVector(*normalised.toTypedArray())
//}
//
//class CoefficientExpr(val value: Int) : Expr, BaseENode("$value")
////class CoefficientENode(val value: Int): BaseENode("$value")
//
//class BladeExpr(val dims: List<String>) : Expr, BaseENode(dims.joinToString(""))
////class BladeENode(val dims: List<String>): BaseENode(dims.joinToString(""))
//
////class BladeTermExpr(val coefficientExpr: CoefficientExpr, bladeExpr: BladeExpr): Expr
////class BladeTermENode(val coefficientEClass: EClass, bladeEClass: EClass): BaseENode(dims.joinToString(""))
//
//class MultiVectorExpr(val components: List<Pair<CoefficientExpr, BladeExpr>>) : Expr
////class MultivectorENode(val components: List<Pair<CoefficientExpr, BladeExpr>>): ENode {
////    override val identifier: String
////        get() = components.joinToString(" + ")
////    override var childEClasses: List<EClass> = listOf()
////    override lateinit var parentEClass: EClass
////}
//
//class DotProductExpr(left: Expr, right: Expr) : BinaryExpr(left, DOT, right)
//class DotProductENode(val leftEClass: EClass, val rightEClass: EClass) : BaseENode("dot")
//
//infix fun Expr.dot(other: Expr) = DotProductExpr(this, other)
//
//class GeometricAlgebraTest : StringSpec({
//    "Basic multivector operations" {
//        MathsContext.empty {
//            withENode { coeff: CoefficientExpr -> coeff }
//            withENode { blade: BladeExpr -> blade }
//            withENode { mv: MultiVectorExpr ->
//                mv.components
//                    .map { (coeff = first, blade = second) -> EBinary(addExpr(coeff), "*", addExpr(blade)) }
//                    .reduce { acc, next -> EBinary(addENode(acc), "+", addENode(next)) }
//            }
//
//            withOperation("+", ::add)
//            withOperation("*", ::geometricProduct)
////            withOperation("dot") { a: MultiVector, b: MultiVector -> MultiVector(a.x + b.x, a.y + b.y) }
////            withOperation("cross") { a: MultiVector, b: MultiVector -> MultiVector(a.x + b.x, a.y + b.y) }
//
//            val a by variable()
//            val b by variable()
//
//            val multiVector = multiVector(2 to x, 1 to y, 3 to z, 4 to xy)
//            a equate multiVector
//
//            val dp = multiVector dot multiVector
//
////            b equate multiVector(x, y, z, xy)
//
////            require(a equal b + multiVector(x, 2 * z, 3 * xy))
//        }
//    }
//})