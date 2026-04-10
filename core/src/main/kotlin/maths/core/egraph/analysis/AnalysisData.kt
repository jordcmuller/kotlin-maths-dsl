package maths.core.egraph.analysis

import maths.core.egraph.EClass
import maths.core.egraph.EGraph
import maths.core.egraph.ENode

interface JoinSemiLattice<T> {
    /**
     * Least upper bound of this and other.
     * Must be:
     * - associative
     * - commutative
     * - idempotent
     */
    fun join(other: T): T
}

interface AnalysisData<T>

data class AnyAnalysisData(val value: Any? = null): AnalysisData<Any>

data class ConstAnalysisData(
    val constValue: ConstValue = ConstValue.Unknown
) : AnalysisData<ConstValue>, JoinSemiLattice<AnalysisData<ConstValue>> {

    override fun join(other: AnalysisData<ConstValue>): AnalysisData<ConstValue> {
        if (other !is ConstAnalysisData) error("Not a ConstAnalysisData")
        return ConstAnalysisData(
            constValue = this.constValue.join(other.constValue)
        )
    }

    companion object {
        val bottom = ConstAnalysisData(ConstValue.Unknown)
    }
}


interface Analysis {

    /**
     * Computes analysis data for a newly created node,
     * using the analysis of its children.
     */
    fun make(eGraph: EGraph, eNode: ENode): AnyAnalysisData

    /**
     * Merge two analysis values when two e-classes union.
     */
    fun join(a: AnyAnalysisData, b: AnyAnalysisData): AnyAnalysisData

    /**
     * Optional hook to inject new structure/unions.
     */
    fun modify(eGraph: EGraph, eClass: EClass)
}

