package maths.core.verification

import maths.core.ast.Expr
import maths.core.state.Equivalence

class EquivalenceClass {
    val expressions = mutableListOf<Expr>()

    fun merge(other: EquivalenceClass): EquivalenceClass {
        expressions.addAll(other.expressions)
        return this
    }

    fun conflictsWith(equivalenceClass: EquivalenceClass): Boolean {
        return false
    }

    override fun toString(): String {
        return "Equivalence Class\n\t" + expressions.joinToString("\n\t")
    }
}

class EquivalenceManager {
    val equivalenceClasses = mutableMapOf<Expr, EquivalenceClass>()

    // create a new equivalence class when an entity is declared
    // that entity is the only thing in this equivalence class
    fun declareVariable(entity: Expr) {
        val equivalenceClass = equivalenceClasses.getOrPut(entity) { EquivalenceClass() }
        equivalenceClass.expressions.add(entity)
    }

    fun equate(left: Expr, right: Expr): Equivalence {
        // when two unknown expressions are equated, check if they belong to the same equivalence class
        val leftClass = equivalenceClasses[left]
        val rightClass = equivalenceClasses[right]

        // if the classes are the same, then they are equivalent
        if (leftClass == rightClass) return Equivalence.True

        if (leftClass == null) {
            rightClass?.expressions?.add(left)
            return Equivalence.True
        }

        if (rightClass == null) {
            leftClass.expressions.add(right)
            return Equivalence.True
        }

        // if they do not, then merge the two equivalence classes

        if (leftClass.conflictsWith(rightClass)) return Equivalence.False

        val mergedClass = leftClass.merge(rightClass)
        equivalenceClasses[right] = mergedClass

        return Equivalence.Unknown
    }

    override fun toString(): String {
        return equivalenceClasses.map { "$it" }.joinToString("\n")
    }

    // TODO: how to check for conflicts between equivalence classes?



}
