package maths.core.egraph

import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.egraph.analysis.OperatorRegistry

open class EGraph {
    lateinit var lowerer: ExprLowerer
    val unionFind = UnionFind<EClass>()
    lateinit var analysis: OperatorRegistry

    val eClasses = mutableListOf<EClass>()
    val eNodes = mutableListOf<ENode>()

    val eNodeHashCons = mutableMapOf<String, EClass>()

    var latestId = 1

    val eClassesToMerge = mutableSetOf<EClass>()
    val staleParentENodes = mutableSetOf<ENode>()
    val staleChildENodes = mutableSetOf<ENode>()

    fun addExpr(expr: Expr): EClass {
        return lowerer.lower(expr, ::addENode)
    }

    fun addENode(eNode: ENode): EClass {
        return findEClass(eNode) ?: createEClass(eNode)
    }

    fun createEClass(eNode: ENode): EClass {
        val newEClass = EClass(latestId++, mutableListOf(eNode))

        processEClass(newEClass)
        processENode(eNode)

        return newEClass
    }

    private fun processEClass(eClass: EClass) {
        unionFind.add(eClass)
        eClasses.add(eClass)
        eClass.nodes.forEach {
            it.parentEClass = eClass
            eClass.analysisData = analysis.join(eClass.analysisData, analysis.make(this, it))

            val value = eClass.analysisData.value
            if (value != null) findEClass(EConst(value))?.let { existingConstEClass -> queueMerge(eClass, existingConstEClass) }

            eNodeHashCons[it.toHashKey] = eClass
        }
    }

    private fun processENode(eNode: ENode) {
        eNode.childEClasses.forEach { child -> child.parentNodes.add(eNode) }
        eNodes.add(eNode)
    }

    fun findCanonicalEClass(eClass: EClass) = eClass.canonicalEClass

    fun findEClass(eNode: ENode) = eNodeHashCons[eNode.toHashKey]?.canonicalEClass

    fun queueMerge(a: EClass, b: EClass): Boolean {
        if (!unionFind.union(a, b)) return false

        with(eClassesToMerge) {
            add(a)
            add(b)
        }

        return true
    }

    fun queueMergeAndRebuild(a: EClass, b: EClass) = queueMerge(a, b).also { rebuild() }

    fun rebuild() {
        while (eClassesToMerge.isNotEmpty()) {
            processPendingMerges()
            canonicalizeStaleNodes()
            propagateCongruence()
        }
    }

    private fun processPendingMerges() {
        val eClassesToProcess = eClassesToMerge.map { it }
        eClassesToMerge.clear()

        val eClassGroups = eClassesToProcess
            .groupBy { it.canonicalEClass }
            .mapValues { (canonicalEClass = key, eClassesPendingMerge = value) ->
                eClassesPendingMerge.filter { it.id != canonicalEClass.id }
            }

        eClassGroups.forEach { (canonicalEClass = key, eClassesPendingMerge = value) ->
            eClassesPendingMerge.forEach {
                handleMerge(canonicalEClass, it)
            }
        }
    }

    private fun handleMerge(canonicalEClass: EClass, eClass: EClass) {
        canonicalEClass.nodes += eClass.nodes
        canonicalEClass.analysisData = analysis.join(canonicalEClass.analysisData, eClass.analysisData)

        eClasses.remove(eClass)
        staleParentENodes.addAll(canonicalEClass.parentNodes + eClass.parentNodes)
        staleChildENodes.addAll(eClass.nodes)
    }

    private fun canonicalizeStaleNodes() {
        updateStaleChildrenENodes()
        updateStaleParentENodes()
    }

    private fun updateStaleChildrenENodes() {
        staleChildENodes.forEach { child ->
            child.parentEClass = child.parentEClass.canonicalEClass
        }
        staleChildENodes.clear()
    }

    private fun updateStaleParentENodes() {
        while (staleParentENodes.isNotEmpty()) {
            val staleParents = staleParentENodes.map { it }
            staleParentENodes.clear()
            staleParents.forEach {
                canonicalizeNode(it)
                recomputeAnalysis(it)
            }
        }
    }

    private fun canonicalizeNode(eNode: ENode) {
        val oldHashCons = eNode.toHashKey
        eNodeHashCons.remove(oldHashCons)

        eNode.childEClasses = eNode.childEClasses.map { it.canonicalEClass }

        eNodeHashCons[eNode.toHashKey] = eNode.parentEClass
    }

    private fun recomputeAnalysis(eNode: ENode) {
        val eClass = eNode.parentEClass

        val newENodeAnalysisData = analysis.make(this, eNode)
        val joined = analysis.join(newENodeAnalysisData, eClass.analysisData)
        eClass.analysisData = joined
        staleParentENodes.addAll(eClass.parentNodes)

        if (joined.value == null) return

        val constEClass = addExpr(Const(joined.value))
        queueMerge(eClass, constEClass)
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
