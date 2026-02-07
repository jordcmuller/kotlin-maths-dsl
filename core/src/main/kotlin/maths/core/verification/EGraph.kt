package maths.core.verification

open class EGraph<ExprType>(val lowerer: ExprLowerer<ExprType>, val builder: ExprBuilder<ExprType>) {
    val unionFind = UnionFind()

    val eClasses = mutableListOf<EClass>()
    val eNodes = mutableListOf<ENode>()

    val eNodeHashCons = mutableMapOf<String, EClassId>()
    val eClassesById = mutableMapOf<EClassId, EClass>()

    var latestId = 1

    val worklist = mutableSetOf<EClassId>()

    fun add(expr: ExprType): EClassId {
        return lowerer.lower(expr, ::add)
    }

    fun add(eNode: ENode): EClassId {
        val representativeEClassId = find(eNode)
        if (representativeEClassId != null) return representativeEClassId

        processENode(eNode)
        return createEClass(eNode).id
    }

    fun createEClass(eNode: ENode): EClass {
        return EClass(latestId++, mutableListOf(eNode)).also {
            processEClass(it)
        }
    }

    private fun processEClass(eClass: EClass) {
        unionFind.add(eClass.id)
        eClasses.add(eClass)
        eClass.nodes.forEach {
            eNodeHashCons[it.toHashKey] = eClass.id
        }
        eClassesById[eClass.id] = eClass
    }

    private fun processENode(eNode: ENode) {
        eNodes.add(eNode)
    }

    fun find(eNode: ENode): EClassId? {
        return unionFind.find(eNodeHashCons[eNode.toHashKey] ?: return null)
    }

    fun merge(a: EClassId, b: EClassId): Boolean {
        if (!unionFind.union(a, b)) return false

        with(worklist) {
            add(a)
            add(b)
        }

        return true
    }

    fun rebuild() {
        // process pending merges
        val idsToProcess = worklist.map { it }
        worklist.clear()

        val groupedEClasses = idsToProcess
            .mapNotNull { eClassesById[it] }
            .groupBy { unionFind.find(it.id) }

        groupedEClasses.forEach { (canonicalId, pendingMergeEClasses) ->
            val canonicalEClass = eClassesById[canonicalId] ?: error("Unable to find canonical EClass during rebuild")

            pendingMergeEClasses
                .filter { it.id != canonicalEClass.id }
                .forEach {
                    canonicalEClass.nodes += it.nodes
                    eClasses.remove(it)
                    eClassesById[it.id] = canonicalEClass
                }
        }

        // canonicalize affected nodes
        eNodes.forEach { it.children = it.children.map(unionFind::find) }

        // replace the entire hashcons
        eNodeHashCons.clear()
        eClasses.forEach { eClass ->
            eClass.nodes.forEach {
                eNodeHashCons[it.toHashKey] = eClass.id
            }
        }

        // detect congruence
        eNodes.groupBy { it.toHashKey }
        // union newly equivalent eclasses
            .forEach { (_, nodes) ->
                nodes.reduce { acc, node ->
                    val a = find(acc) ?: error("Unable to find node")
                    val b = find(node) ?: error("Unable to find node")
                    merge(a, b)
                    acc
                }
            }
    }

    fun mergeAndRebuild(a: EClassId, b: EClassId) = merge(a, b).also { rebuild() }

    override fun toString() = print()
}

val ENode.toHashKey get() = buildString {
    append(identifier)
    if (children.isNotEmpty()) {
        append("(${children.joinToString(" ")})")
    }
}
