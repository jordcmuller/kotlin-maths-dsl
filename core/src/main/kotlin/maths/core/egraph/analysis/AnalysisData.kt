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

data class AnalysisData(
    val constValue: ConstValue = ConstValue.Unknown
) : JoinSemiLattice<AnalysisData> {

    override fun join(other: AnalysisData): AnalysisData {
        return AnalysisData(
            constValue = this.constValue.join(other.constValue)
        )
    }

    companion object {
        val bottom = AnalysisData(ConstValue.Unknown)
    }
}


interface Analysis {

    /**
     * Computes analysis data for a newly created node,
     * using the analysis of its children.
     */
    fun make(eGraph: EGraph, eNode: ENode): AnalysisData

    /**
     * Merge two analysis values when two e-classes union.
     */
    fun join(a: AnalysisData, b: AnalysisData): AnalysisData = a.join(b)

    /**
     * Optional hook to inject new structure/unions.
     */
    fun modify(eGraph: EGraph, eClass: EClass)
}

