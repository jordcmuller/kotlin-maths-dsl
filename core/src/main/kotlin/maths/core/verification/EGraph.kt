package maths.core.verification

open class EGraph<ExprType>(val lowerer: ExprLowerer<ExprType>, val builder: ExprBuilder<ExprType>) {
    val unionFind = UnionFind<EClass>()

    val eClasses = mutableListOf<EClass>()
    val eNodes = mutableListOf<ENode>()

    val eNodeHashCons = mutableMapOf<String, EClass>()
    val eClassesById = mutableMapOf<EClassId, EClass>()

    var latestId = 1

    val worklist = mutableSetOf<EClass>()

    fun add(expr: ExprType): EClass {
        return lowerer.lower(expr, ::add)
    }

    fun add(eNode: ENode): EClass {
        return findEClass(eNode) ?: createEClass(eNode)
    }

    fun createEClass(eNode: ENode): EClass {
        return EClass(latestId++, mutableListOf(eNode)).also {
            processEClass(it)

            eNode.parent = it
            eNodes.add(eNode)
        }
    }

    private fun processEClass(eClass: EClass) {
        unionFind.add(eClass)
        eClasses.add(eClass)
        eClass.nodes.forEach {
            eNodeHashCons[it.toHashKey] = eClass
        }
        eClassesById[eClass.id] = eClass
    }

    fun findEClass(eNode: ENode): EClass? {
        return unionFind.find(eNodeHashCons[eNode.toHashKey] ?: return null)
    }

    fun merge(a: EClass, b: EClass): Boolean {
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
            .groupBy { unionFind.find(it) }

        groupedEClasses.forEach { (canonicalEClass, pendingMergeEClasses) ->
            pendingMergeEClasses
                .filter { it.id != canonicalEClass.id }
                .forEach {
                    canonicalEClass.nodes += it.nodes
                    eClasses.remove(it)
                    eClassesById[it.id] = canonicalEClass // todo: should this not just be removed?
                }
        }

        // canonicalize affected nodes
        eNodes.forEach { it.children = it.children.map(unionFind::find) }

        // replace the entire hashcons
        eNodeHashCons.clear()
        eClasses.forEach { eClass ->
            eClass.nodes.forEach {
                eNodeHashCons[it.toHashKey] = eClass
            }
        }

        // detect congruence
        eNodes.groupBy { it.toHashKey }
        // union newly equivalent eclasses
            .forEach { (_, nodes) ->
                nodes.reduce { acc, node ->
                    val a = findEClass(acc) ?: error("Unable to find node")
                    val b = findEClass(node) ?: error("Unable to find node")
                    merge(a, b)
                    acc
                }
            }
    }

    fun mergeAndRebuild(a: EClass, b: EClass) = merge(a, b).also { rebuild() }

    override fun toString() = print()
}

val ENode.toHashKey get() = buildString {
    append(identifier)
    if (children.isNotEmpty()) {
        append("(${children.joinToString(" ")})")
    }
}
