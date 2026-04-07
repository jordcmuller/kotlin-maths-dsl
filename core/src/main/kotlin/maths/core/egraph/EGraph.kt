package maths.core.egraph

import maths.core.ast.Expr
import maths.core.egraph.analysis.ConstFoldingAnalysis
import maths.core.egraph.analysis.ConstValue

open class EGraph {
    val lowerer = ExprLowerer()
    val unionFind = UnionFind<EClass>()
    val analysis = ConstFoldingAnalysis()

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
            eClass.analysisData = analysis.join(eClass.analysisData, analysis.make(this, it))
            eNodeHashCons[it.toHashKey] = eClass
        }
    }

    fun findEClass(eClass: EClass) = eClass.canonicalEClass

    fun findEClass(eNode: ENode) = eNodeHashCons[eNode.toHashKey]?.canonicalEClass

    fun queueMerge(a: EClass, b: EClass): Boolean {
        if (!unionFind.union(a, b)) return false

        with(worklist) {
            add(a)
            add(b)
        }

        return true
    }

    fun queueMergeAndRebuild(a: EClass, b: EClass) = queueMerge(a, b).also { rebuild() }

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
            .mapValues { (canonicalEClass, eClassesPendingMerge) ->
                eClassesPendingMerge.filter { it.id != canonicalEClass.id }
            }

        eClassGroups.forEach { (canonicalEClass, eClassesPendingMerge) ->
            eClassesPendingMerge.forEach {
                handleMerge(canonicalEClass, it)
            }
        }
    }

    private fun handleMerge(canonicalEClass: EClass, eClass: EClass) {
        val newAnalysisData = analysis.join(canonicalEClass.analysisData, eClass.analysisData)

        if (newAnalysisData.constValue == ConstValue.Conflict) TODO("should we handle conflicts here?")

        eClasses.remove(eClass)

        canonicalEClass.analysisData = newAnalysisData
        analysis.modify(this, canonicalEClass)
        canonicalEClass.nodes += eClass.nodes
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
        val congruentNodeGroups = eNodes.groupBy { it.toHashKey }.values
        congruentNodeGroups.forEach { congruentNodes -> mergeAllNodes(congruentNodes) }
    }

    private fun mergeAllNodes(congruentNodes: List<ENode>) {
        congruentNodes.reduce { a, b -> a queueMerge b }
    }

    override fun toString() = print()

    private val EClass.canonicalEClass: EClass get() = unionFind.find(this)
    private infix fun ENode.queueMerge(other: ENode) = also { queueMerge(this.parentEClass, other.parentEClass) }
}

val ENode.toHashKey get() = buildString {
    append(identifier)
    if (childEClasses.isNotEmpty()) {
        append("(${childEClasses.map { it.id }.joinToString(" ") })")
    }
}
