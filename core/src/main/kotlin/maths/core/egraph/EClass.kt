package maths.core.egraph

import maths.core.egraph.analysis.AnalysisData

class EClass(val id: Int, val nodes: MutableList<ENode> = mutableListOf()) {
    var analysisData = AnalysisData()

    override fun hashCode() = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EClass

        return id == other.id
    }

    override fun toString(): String {
        return "${nodes.first()}"
    }
}