package maths.core.verification

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Func
import maths.core.ast.Neg
import maths.core.ast.Operation
import maths.core.ast.Pow
import maths.core.ast.Var

// Result of attempting to validate/add an equality
sealed class AddEqualityResult {
    object AlreadyEquivalent : AddEqualityResult()
    object Unified : AddEqualityResult()
    data class Conflict(val reason: String) : AddEqualityResult()
}

class CongruenceClosure {
    // internal integer node id for compact union-find and maps
    private var nextId = 1
    private fun newId() = nextId++

    // maps an Expr -> node id (ensures structurally identical Exprs share a node)
    private val exprToId = mutableMapOf<Expr, Int>()

    // id -> Expr (for debugging / diagrams)
    private val idToExpr = mutableMapOf<Int, Expr>()

    // DAG parents: childId -> set of parent nodeIds
    private val parents = mutableMapOf<Int, MutableSet<Int>>()

    // union-find
    private val parent = mutableMapOf<Int, Int>()
    private val rank = mutableMapOf<Int, Int>()

    // If an equivalence class contains a Const, record it here: repId -> ConstExpr
    private val classConst = mutableMapOf<Int, Const>()

    // Signature map: (op, list of child-rep-ids) -> nodeId
    private data class SigKey(val op: Operation, val childRepIds: List<Int>)
    private val signature = mutableMapOf<SigKey, Int>()

    // For detected conflicts
    val conflicts = mutableListOf<String>()

    // ---- helpers ----
    private fun find(x: Int): Int {
        val p = parent.getOrDefault(x, x)
        if (p != x) {
            val r = find(p)
            parent[x] = r
            return r
        }
        return x
    }

    private fun unionRoots(aRoot: Int, bRoot: Int): Boolean {
        var ra = find(aRoot)
        var rb = find(bRoot)
        if (ra == rb) return true

        // check constants conflict
        val ca = classConst[ra]
        val cb = classConst[rb]
        if (ca != null && cb != null && ca.value != cb.value) {
            // conflict: same class would contain two different constants
            val msg = "Conflict: trying to equate constant ${ca.value} with ${cb.value}"
            conflicts += msg
            return false
        }

        // union by rank
        val raRank = rank.getOrDefault(ra, 0)
        val rbRank = rank.getOrDefault(rb, 0)
        if (raRank < rbRank) {
            parent[ra] = rb
            ra = rb // new representative
        } else {
            parent[rb] = ra
            if (raRank == rbRank) rank[ra] = raRank + 1
        }
        val newRep = find(ra)
        // merge constant info
        if (ca != null) classConst[newRep] = ca
        else if (cb != null) classConst[newRep] = cb

        // merge parent sets and update them
        val mergedParents = mutableSetOf<Int>()
        parents.remove(aRoot)?.let { mergedParents.addAll(it) }
        parents.remove(bRoot)?.let { mergedParents.addAll(it) }
        if (mergedParents.isNotEmpty()) parents.getOrPut(newRep) { mutableSetOf() }.addAll(mergedParents)

        // After merging, need to check congruence: any two parents with same signature must be merged
        // Build a temporary queue of parent nodes to re-check signatures
        val queue = ArrayDeque<Int>()
        mergedParents.forEach { queue.add(it) }

        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            // compute p's signature based on current representatives of its children
            val expr = idToExpr[p]!!
            if (expr is BinaryExpr) {
                val childRepIds = listOf(expr.left, expr.right).map { find(makeNode(it)) } // makeNode safe: reuses existing nodes
                val key = SigKey(expr.operation, childRepIds)
                val existing = signature[key]
                if (existing == null) {
                    signature[key] = find(p)
                } else {
                    val exRep = find(existing)
                    val pRep = find(p)
                    if (exRep != pRep) {
                        // union the two parent nodes (propagation). If this union conflicts, report it.
                        val ok = unionRoots(exRep, pRep)
                        if (!ok) return false
                        // after a successful union, add merged parent reps' parents to queue
                        find(exRep).let { rep ->
                            parents[rep]?.forEach { queue.add(it) }
                        }
                    }
                }
            } else {
                // leaf node (Var or Const) - record signature for leaf "op" if you want. Usually not needed.
            }
        }

        return true
    }

    // Ensure a node exists for the given Expr; return its node id.
    // Does NOT perform substitutions or normalisation.
    fun makeNode(expr: Expr): Int {
        exprToId[expr]?.let { return it }

        val id = newId()
        exprToId[expr] = id
        idToExpr[id] = expr
        parent[id] = id
        rank[id] = 0

        when (expr) {
            is Const -> {
                // record class constant for the singleton class
                classConst[id] = expr
            }
            is BinaryExpr -> {
                // ensure children exist and add this id to each child's parent set
                val childIds = listOf(expr.left, expr.right).map { makeNode(it) }
                childIds.forEach { cid ->
                    parents.getOrPut(cid) { mutableSetOf() }.add(id)
                }
                // compute signature with current child representatives and link via signature map:
                val childReps = childIds.map { find(it) }
                val key = SigKey(expr.operation, childReps)
                val existing = signature[key]
                if (existing != null) {
                    // existing congruent node found -> union them (propagate)
                    val ok = unionRoots(existing, id)
                    if (!ok) {
                        // conflict detected during creation (e.g. constants clash)
                        return id
                    }
                } else {
                    signature[key] = id
                }
            }
            is Var -> {
                // nothing else
            }

            is Func -> TODO()
            is Neg -> TODO()
            is Pow -> TODO()
        }
        return id
    }

    /**
     * Validate / add equality lhs == rhs. This DOES NOT rewrite lhs/rhs.
     * Returns AddEqualityResult indicating success or conflict.
     */
    fun addEquality(lhs: Expr, rhs: Expr): AddEqualityResult {
        val leftId = makeNode(lhs)
        val rightId = makeNode(rhs)

        val lRep = find(leftId)
        val rRep = find(rightId)
        if (lRep == rRep) return AddEqualityResult.AlreadyEquivalent

        val ok = unionRoots(lRep, rRep)
        return if (ok) AddEqualityResult.Unified
               else AddEqualityResult.Conflict(conflicts.last())
    }

    // Helpful debugging / diagnostic view of equivalence classes (repId -> members)
    fun equivalenceClasses(): Map<Int, List<Int>> {
        val map = mutableMapOf<Int, MutableList<Int>>()
        idToExpr.keys.forEach { id ->
            val r = find(id)
            map.getOrPut(r) { mutableListOf() }.add(id)
        }
        return map
    }

    // Degrees of freedom (simple): count distinct reps that are Vars and not bound to a Const
    fun degreesOfFreedom(): Int {
        val varReps = idToExpr.entries
            .filter { it.value is Var }
            .map { find(it.key) }
            .toSet()
        return varReps.count { classConst[it] == null }
    }
}
