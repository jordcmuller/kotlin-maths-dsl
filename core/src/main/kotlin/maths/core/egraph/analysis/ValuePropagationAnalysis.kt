package maths.core.egraph.analysis

import maths.core.ast.Const
import maths.core.dsl.MathsContext
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.egraph.EBinary
import maths.core.egraph.EClass
import maths.core.egraph.EConst
import maths.core.egraph.EGraph
import maths.core.egraph.ENode
import maths.core.rewriting.dsl.invoke
import kotlin.reflect.KClass

//class ValuePropagationAnalysis : Analysis {
//
//    override fun make(eGraph: EGraph, eNode: ENode): AnalysisData {
//        // TODO: create a factory here that works with the following dimensions
//        // 1. operation symbol
//        // 2. operand types
//        return when (eNode) {
//            is EConst -> ConstAnalysisData(eNode.value)
//
//            is EBinary if (eNode.operation == "+") -> {
//                val left = eGraph.findEClass(eNode.left).analysisData.constValue
//                val right = eGraph.findEClass(eNode.right).analysisData.constValue
//                ConstAnalysisData(evalAdd(left, right))
//            }
//
//            is EBinary if (eNode.operation == "*") -> {
//                val left = eGraph.findEClass(eNode.left).analysisData.constValue
//                val right = eGraph.findEClass(eNode.right).analysisData.constValue
//                ConstAnalysisData(evalMul(left, right))
//            }
//
//            else -> AnalysisData.bottom
//        }
//    }
//
//    override fun modify(eGraph: EGraph, eClass: EClass) {
//        val cv = eClass.analysisData.constValue
//
//        if (cv is ConstValue.Unknown || cv is ConstValue.Conflict) return
//
//        // Ensure a literal Const node exists, then union it into this eclass.
//        val constEClass = eGraph.add(EConst(cv))
//        eGraph.queueMerge(eClass, constEClass)
//    }
//
//    private fun evalAdd(a: ConstValue, b: ConstValue): ConstValue {
//        if (a is ConstValue.IntVal && b is ConstValue.IntVal) {
//            return ConstValue.IntVal(a.value + b.value)
//        }
//        // Extend: rational + rational, int + rational, etc.
//        return ConstValue.Unknown
//    }
//
//    private fun evalMul(a: ConstValue, b: ConstValue): ConstValue {
//        if (a is ConstValue.IntVal && b is ConstValue.IntVal) {
//            return ConstValue.IntVal(a.value * b.value)
//        }
//        return ConstValue.Unknown
//    }
//
////    private fun evalNeg(x: ConstValue): ConstValue {
////        if (x is ConstValue.IntVal) {
////            return ConstValue.IntVal(x.value.negate())
////        }
////        return ConstValue.Unknown
////    }
//}

class OperatorRegistry: Analysis {
    val map = mutableMapOf<String, OperationRegistry>()
    override fun make(
        eGraph: EGraph,
        eNode: ENode
    ): AnyAnalysisData {
        return when (eNode) {
            is EConst -> AnyAnalysisData(eNode.value)

            is EBinary -> {
                val operations = map[eNode.operation]
                val left = eGraph.findEClass(eNode.left).analysisData.value ?: return AnyAnalysisData()
                val right = eGraph.findEClass(eNode.right).analysisData.value ?: return AnyAnalysisData()

                val operation = operations?.get(left, right) ?: return AnyAnalysisData()

                val result = operation(left, right)
                AnyAnalysisData(result)
            }

            else -> AnyAnalysisData()
        }
    }

    override fun join(
        a: AnyAnalysisData,
        b: AnyAnalysisData
    ): AnyAnalysisData {
        if (a.value == null && b.value == null) return AnyAnalysisData()

        if (a.value == null) return AnyAnalysisData(b.value)
        if (b.value == null) return AnyAnalysisData(a.value)

        if (a.value == b.value) return AnyAnalysisData(a.value)
        error("Conflict: a = ${a.value} != b = ${b.value}")
    }

    override fun modify(eGraph: EGraph, eClass: EClass) {
        val value = eGraph.findEClass(eClass).analysisData.value as? Int ?: return

        val constEClass = eGraph.add(Const(value))
        eGraph.queueMerge(eClass, constEClass)
    }
}

class OperationRegistry(val operator: String) {

    val functionMap = mutableMapOf<List<KClass<*>>, Function<*>>()

    inline fun <reified T1 : Any> register(noinline function: (T1) -> Any?) {
        val key = listOf(T1::class)
        // todo: require that only one key is allowed, no type overloading
        functionMap[key] = function
    }

    inline fun <reified T1 : Any, reified T2 : Any> register(noinline function: (T1, T2) -> Any) {
        val key = listOf(T1::class, T2::class)
        // todo: require that only one key is allowed, no type overloading
        functionMap[key] = function
    }

    inline fun <reified T1 : Any, reified T2 : Any> get(): (T1, T2) -> Any {
        val key = listOf(T1::class, T2::class)
        val func = functionMap[key] ?: throw IllegalArgumentException("No function registered for types: $key")
        return func as (T1, T2) -> Any
    }

    fun <T1 : Any, T2 : Any> get(item1: T1, item2: T2): (T1, T2) -> Any {
        val key = listOf(item1::class, item2::class)
        val func = functionMap[key] ?: throw IllegalArgumentException("No function registered for types: $key")
        return func as (T1, T2) -> Any
    }

}

fun main() {
    MathsContext.empty {
        withOperation("+") { a: Int, b: Int -> a + b }
        withOperation("*") { a: Int, b: Int -> a * b }

        // register axioms
        val x by variable()
        val y by variable()
        withRule { "additiveCommutativity" { x + y to y + x } }
        withRule { "multiplicativeCommutativity" { x * y to y * x } }

        require(x + y equal y + x)

        x equate 1

        require(x equal 1)
        require(x + y equal y + 1)

        y equate 3

        require(y equal 3)
        require(x + y equal 4)
    }

    // operator '+' exists
    // register add for int and int type combo
    fun add(a: ConstValue.IntVal, b: ConstValue.IntVal): ConstValue {
        return ConstValue.IntVal(a.value + b.value)
    }

    fun addIntValAndInt(a: ConstValue.IntVal, b: Int): ConstValue {
        return ConstValue.IntVal(a.value + b)
    }

    val registry = OperationRegistry("+")

    registry.register(::add)
    registry.register(::addIntValAndInt)

    val addFunc = registry.get<ConstValue.IntVal, ConstValue.IntVal>()
    val addAgain = registry.get(ConstValue.IntVal(1), 2)

    val result = addFunc(ConstValue.IntVal(1), ConstValue.IntVal(2))
    val result3 = addAgain(ConstValue.IntVal(10), 10)

    println(result)
    println(result3)
}













