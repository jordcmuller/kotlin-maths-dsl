package maths.core.verification

class UnionFind<T> {
    val parents = mutableMapOf<T, T>()

    fun add(item: T) {
        parents.getOrPut(item) { item }
    }

    fun find(item: T): T {
        if (item.isRepresentative) return item

        item.parent = item.parent.representative
        return item.parent.representative
    }

    fun union(a: T, b: T): Boolean {
        if (a isInSameEClassAs b) return false

        b.representative.parent = a.representative
        return true
    }

    private var T.parent: T
        get() = parents[this] ?: error("Non-existent item $this in UnionFind")
        set(value) { parents[this] = value }
    private val T.representative get() = find(this)
    private val T.isRepresentative get() = this == parent
    private infix fun T.isInSameEClassAs(other: T) = representative == other.representative
}