package maths.core.verification

class EGraph<ExprType>(val lowerer: ExprLowerer<ExprType>) {
    val unionFind = UnionFind()

    val eClasses = mutableListOf<EClass>()
    val eNodes = mutableListOf<ENode>()

    val eNodeHashCons = mutableMapOf<String, EClassId>()
    val eClassesById = mutableMapOf<EClassId, EClass>()

    var latestId = 0

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
            eNodeHashCons[eNodeKey(it)] = eClass.id
        }
        eClassesById[eClass.id] = eClass
    }

    private fun processENode(eNode: ENode) {
        eNodes.add(eNode)
    }

    fun find(eNode: ENode): EClassId? {
        return unionFind.find(eNodeHashCons[eNodeKey(eNode)] ?: return null)
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

        groupedEClasses.forEach { (canonicalId, eClasses) ->
            val canonicalEClass = eClassesById[canonicalId] ?: error("Unable to find canonical EClass during rebuild")

            eClasses
                .filter { it.id != canonicalEClass.id }
                .forEach { combine(canonicalEClass, it) }
        }

        // canonicalize affected nodes
        eNodes.forEach { it.children = it.children.map(unionFind::find) }

        // detect congruence
        eNodes.groupBy { eNodeKey(it) }
        // union newly equivalent eclasses
            .forEach { (_, nodes) ->
                nodes.reduce { acc, node ->
                    val a = find(acc) ?: error("Unable to find node")
                    val b = find(node) ?: error("Unable to find node")
                    merge(a, b)
                    acc
                }
            }

        // replace the entire hashcons
        eNodeHashCons.clear()
        eClasses.forEach { eClass ->
            eClass.nodes.forEach {
                eNodeHashCons[eNodeKey(it)] = eClass.id
            }
        }
    }

    fun eNodeKey(eNode: ENode) = buildString {
        append(eNode.identifier)
        if (eNode.children.isNotEmpty()) {
            append("(${eNode.children.joinToString(" ")})")
        }
    }

    fun combine(a: EClass, b: EClass) {
        a.nodes += b.nodes
        disposeEClass(b)
    }

    private fun disposeEClass(b: EClass) {
        eClasses.remove(b)
        eClassesById.remove(b.id)
    }

    fun mergeAndRebuild(a: EClassId, b: EClassId) = merge(a, b).also { rebuild() }
}
