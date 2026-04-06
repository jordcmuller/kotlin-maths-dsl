package maths.core.egraph.analysis

import maths.core.egraph.EBinary
import maths.core.egraph.EClass
import maths.core.egraph.EConst
import maths.core.egraph.EGraph
import maths.core.egraph.ENode
import maths.core.egraph.EVar

class ConstFoldingAnalysis : Analysis {

    override fun make(eGraph: EGraph, eNode: ENode): AnalysisData {
        return when (eNode) {
            is EConst -> AnalysisData(eNode.value)

            is EBinary if (eNode.operation == "+") -> {
                val left = eGraph.findEClass(eNode.left).analysisData.constValue
                val right = eGraph.findEClass(eNode.right).analysisData.constValue
                AnalysisData(evalAdd(left, right))
            }

            is EBinary if (eNode.operation == "*") -> {
                val left = eGraph.findEClass(eNode.left).analysisData.constValue
                val right = eGraph.findEClass(eNode.right).analysisData.constValue
                AnalysisData(evalMul(left, right))
            }

            else -> AnalysisData.bottom
        }
    }

    override fun modify(eGraph: EGraph, eClass: EClass) {
        val cv = eClass.analysisData.constValue

        if (cv is ConstValue.Unknown || cv is ConstValue.Conflict) return

        // Ensure a literal Const node exists, then union it into this eclass.
        val constEClass = eGraph.add(EConst(cv))
        eGraph.queueMerge(eClass, constEClass)
    }

    private fun evalAdd(a: ConstValue, b: ConstValue): ConstValue {
        if (a is ConstValue.IntVal && b is ConstValue.IntVal) {
            return ConstValue.IntVal(a.value + b.value)
        }
        // Extend: rational + rational, int + rational, etc.
        return ConstValue.Unknown
    }

    private fun evalMul(a: ConstValue, b: ConstValue): ConstValue {
        if (a is ConstValue.IntVal && b is ConstValue.IntVal) {
            return ConstValue.IntVal(a.value * b.value)
        }
        return ConstValue.Unknown
    }

//    private fun evalNeg(x: ConstValue): ConstValue {
//        if (x is ConstValue.IntVal) {
//            return ConstValue.IntVal(x.value.negate())
//        }
//        return ConstValue.Unknown
//    }
}