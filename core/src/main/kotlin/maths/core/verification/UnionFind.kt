package maths.core.verification

class UnionFind {
    val parents = mutableMapOf<EClassId, EClassId>()

    fun add(eClassId: EClassId) {
        parents.getOrPut(eClassId) { eClassId }
    }

    fun find(eClassId: EClassId): EClassId {
        if (eClassId.isRepresentative) return eClassId

        eClassId.parent = eClassId.parent.representative
        return eClassId.parent.representative
    }

    fun union(a: EClassId, b: EClassId): EClassId {
        if (a isNotInSameEClassAs b) b.representative.parent = a.representative

        return a.representative
    }

    private var EClassId.parent
        get() = parents[this] ?: error("Non-existent eClassId")
        set(value) { parents[this] = value }
    private val EClassId.representative get() = find(this)
    private val EClassId.isRepresentative get() = this == parent
    private infix fun EClassId.isNotInSameEClassAs(other: EClassId) = representative != other.representative
}