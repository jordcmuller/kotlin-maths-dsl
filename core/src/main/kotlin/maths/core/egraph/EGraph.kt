package maths.core.egraph

import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.egraph.analysis.OperatorRegistry

open class EGraph {
    val lowerer = ExprLowerer()
    val unionFind = UnionFind<EClass>()
    lateinit var analysis: OperatorRegistry

    val eClasses = mutableListOf<EClass>()
    val eNodes = mutableListOf<ENode>()

    val eNodeHashCons = mutableMapOf<String, EClass>()

    var latestId = 1

    val eClassesToMerge = mutableSetOf<EClass>()
    val staleENodes = mutableSetOf<ENode>()

    fun add(expr: Expr): EClass {
        return lowerer.lower(expr, ::add).also { rebuild() }
    }

    fun add(eNode: ENode): EClass {
        return findEClass(eNode) ?: createEClass(eNode)
    }

    fun createEClass(eNode: ENode): EClass {
        val newEClass = EClass(latestId++, mutableListOf(eNode))

        processEClass(newEClass)
        processENode(newEClass, eNode)

        return newEClass
    }

    private fun processEClass(eClass: EClass) {
        unionFind.add(eClass)
        eClasses.add(eClass)
        eClass.nodes.forEach {
            eClass.analysisData = analysis.join(eClass.analysisData, analysis.make(this, it))
            staleENodes.addAll(eClass.parentNodes)
            eNodeHashCons[it.toHashKey] = eClass
        }
    }

    private fun processENode(eClass: EClass, eNode: ENode) {
        eNode.parentEClass = eClass
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
        while (eClassesToMerge.isNotEmpty() || staleENodes.isNotEmpty()) {
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
        canonicalEClass.nodes += eClass.nodes
        canonicalEClass.analysisData = analysis.join(canonicalEClass.analysisData, eClass.analysisData)

        eClasses.remove(eClass)
        staleENodes.addAll(eClass.parentNodes)
        staleENodes.addAll(eClass.nodes)
    }

    private fun canonicalizeStaleNodes() {
        val eNodesToProcess = staleENodes.map { it }
        staleENodes.clear()
        eNodesToProcess.forEach {
            canonicalizeNode(it)
            recomputeAnalysis(it)
        }
    }

    private fun canonicalizeNode(eNode: ENode) {
        val oldHashCons = eNode.toHashKey
        eNodeHashCons.remove(oldHashCons)

        eNode.parentEClass = eNode.parentEClass.canonicalEClass
        eNode.childEClasses = eNode.childEClasses.map { it.canonicalEClass }

        eNodeHashCons[eNode.toHashKey] = eNode.parentEClass
    }

    private fun recomputeAnalysis(eNode: ENode) {
        val eClass = eNode.parentEClass

        val newENodeAnalysisData = analysis.make(this, eNode)
        val joined = analysis.join(newENodeAnalysisData, eClass.analysisData)
        eClass.analysisData = joined
        staleENodes.addAll(eClass.parentNodes)

        if (joined.value == null) return

        val constEClass = add(Const(joined.value))
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
