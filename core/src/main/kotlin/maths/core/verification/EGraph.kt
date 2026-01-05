package maths.core.verification

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Var
import maths.core.dsl.maths

class EGraph {
    val eclasses = mutableMapOf<EClassId, EClass>()
    val hashcons = mutableMapOf<String, EClassId>()
    val parents = mutableMapOf<EClassId, MutableSet<ParentRef>>()

    val worklist = mutableListOf<ParentRef>()

    private fun hash(expr: Expr): String {
        return when (expr) {
            is BinaryExpr -> hash(expr.left) + expr.operation.symbol + "(${hash(expr.right)})"
            is Var -> expr.name
            is Const -> expr.value.toString()
            else -> TODO("Missing expr type")
        }
    }

    private fun hash(node: ENode): String {
        return "(${node.operation} ${node.children.joinToString(", ")})"
    }

    private fun createNewNode(expr: Expr) = when (expr) {
        is BinaryExpr -> ENode(expr.operation.symbol, mutableListOf(add(expr.left), add(expr.right)))
        is Var -> ENode(expr.name)
        is Const -> ENode(expr.value.toString())
        else -> TODO("Missing expr type")
    }

    fun add(expr: Expr): EClassId {
        val hash = hash(expr)

        val existingEClass = hashcons[hash]
        if (existingEClass != null) return existingEClass

        val newNode = createNewNode(expr)

        val newId = eclasses.size
        hashcons[hash] = newId

        val newEClass = EClass(newId, mutableListOf(newNode))

        eclasses[newId] = newEClass

        newNode.children.forEach { childId ->
            val parentSet = parents.getOrPut(childId) { mutableSetOf() }
            parentSet += ParentRef(newNode, newId)
        }

        return newId
    }

    fun find(eClassId: EClassId): EClass? {
        TODO()
    }

    fun union(a: EClassId, b: EClassId): Boolean {
        val ra = eclasses[a] ?: error("Non-existent EClass")
        val rb = eclasses[b] ?: error("Non-existent EClass")
        if (ra == rb) return false

        ra.nodes.addAll(rb.nodes)
        eclasses[b] = ra

        return true
    }

    fun merge(a: EClassId, b: EClassId): Boolean {
        if (!union(a, b)) return false

        worklist += (parents[a] ?: setOf())
        worklist += (parents[b] ?: setOf())

        return true
    }

    fun rebuild() {
        val groups = mutableMapOf<String, EClassId>()

        // update the children for all the parents to the main equivalence class id
        while (worklist.isNotEmpty()) {
            val parent = worklist.removeAt(0)
            val children = parent.enode.children
            for ((i, childEClass) in children.withIndex()) {
                children[i] = eclasses[childEClass]?.id ?: error("Non-existent EClass")
            }

            // add to group based on hash of operation and children eClassIds
            val nodeHash = hash(parent.enode)

            val priorMatchingEClassId = groups[nodeHash]
            if (priorMatchingEClassId != null)
                merge(priorMatchingEClassId, parent.eClassId)
            else groups[nodeHash] = parent.eClassId
        }
    }

    fun ematch() {}
}

typealias EClassId = Int

data class EClass(
    val id: EClassId,
    val nodes: MutableList<ENode>
)

data class ENode(
    val operation: String,
    val children: MutableList<EClassId> = mutableListOf(),
)

data class ParentRef(
    val enode: ENode,
    val eClassId: EClassId
)


fun main() {
    val graph = EGraph()
    maths {
        val x by variable()
        val y by variable()
        val eclass1 = graph.add(x)
        val eclass2 = graph.add(y)
        val eclass3 = graph.add(1.c)
        graph.add(x + 1)
        graph.add(y + 1)
        graph.add(y + 1)
        graph.merge(eclass1, eclass2)
        graph.merge(eclass1, eclass3)
        graph.rebuild()
    }
}