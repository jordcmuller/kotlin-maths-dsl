package maths.core.verification

import maths.core.ast.Expr

open class EGraph {
    val lowerer = ExprLowerer()
    val unionFind = UnionFind<EClass>()

    val eClasses = mutableListOf<EClass>()
    val eNodes = mutableListOf<ENode>()

    val eNodeHashCons = mutableMapOf<String, EClass>()

    var latestId = 1

    val worklist = mutableSetOf<EClass>()

    fun add(expr: Expr): EClass {
        return lowerer.lower(expr, ::add)
    }

    fun add(eNode: ENode): EClass {
        return findEClass(eNode) ?: createEClass(eNode)
    }

    fun createEClass(eNode: ENode): EClass {
        return EClass(latestId++, mutableListOf(eNode)).also {
            processEClass(it)

            eNode.parentEClass = it
            eNodes.add(eNode)
        }
    }

    private fun processEClass(eClass: EClass) {
        unionFind.add(eClass)
        eClasses.add(eClass)
        eClass.nodes.forEach {
            eNodeHashCons[it.toHashKey] = eClass
        }
    }

    fun findEClass(eClass: EClass) = eClass.canonicalEClass

    fun findEClass(eNode: ENode): EClass? {
        return eNodeHashCons[eNode.toHashKey]?.canonicalEClass
    }

    fun merge(a: EClass, b: EClass): Boolean {
        if (!unionFind.union(a, b)) return false

        with(worklist) {
            add(a)
            add(b)
        }

        return true
    }

    fun mergeAndRebuild(a: EClass, b: EClass) = merge(a, b).also { rebuild() }

    fun rebuild() {
        while (worklist.isNotEmpty()) {
            processPendingMerges()
            canonicalizeAllNodes()
            propagateCongruence()
        }
    }

    private fun processPendingMerges() {
        val eClassesToProcess = worklist.map { it }
        worklist.clear()

        val eClassGroups = eClassesToProcess
            .groupBy { it.canonicalEClass }

        eClassGroups.forEach { (canonicalEClass, eClassesPendingMerge) ->
            eClassesPendingMerge
                .filter { it.id != canonicalEClass.id }
                .forEach { eClass ->
                    eClasses.remove(eClass)

                    canonicalEClass.nodes += eClass.nodes
                }
        }
    }

    private fun canonicalizeAllNodes() {
        eNodes.forEach { eNode ->
            val oldHashCons = eNode.toHashKey
            val oldParent = eNode.parentEClass

            eNode.parentEClass = eNode.parentEClass.canonicalEClass
            eNode.childEClasses = eNode.childEClasses.map { it.canonicalEClass }

            val newHashCons = eNode.toHashKey
            val newParent = eNode.parentEClass

            val nothingHasChanged = oldParent == newParent && oldHashCons == newHashCons
            if (nothingHasChanged) return@forEach

            eNodeHashCons.remove(oldHashCons)
            eNodeHashCons[newHashCons] = newParent
        }
    }

    private fun propagateCongruence() {
        val congruentNodeGroups = eNodes.groupBy { it.toHashKey }.map { it.value }
        congruentNodeGroups.forEach { congruentNodes -> mergeAllNodes(congruentNodes) }
    }

    private fun mergeAllNodes(congruentNodes: List<ENode>) {
        congruentNodes.reduce { a, b -> a merge b }
    }

    override fun toString() = print()

    private val EClass.canonicalEClass: EClass get() = unionFind.find(this)
    private infix fun ENode.merge(other: ENode) = also { merge(this.parentEClass, other.parentEClass) }
}

val ENode.toHashKey get() = buildString {
    append(identifier)
    if (childEClasses.isNotEmpty()) {
        append("(${childEClasses.map { it.id }.joinToString(" ") })")
    }
}
