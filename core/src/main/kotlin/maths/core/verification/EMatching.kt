package maths.core.verification

import maths.core.ast.Add
import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Operation
import maths.core.ast.Var
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v

fun eMatch(eGraph: EGraph<Expr>, exprToFind: Expr): List<Expr> {
    return eMatch(eGraph, exprToFind.toEMatcher())
}

fun eMatch(eGraph: EGraph<Expr>, eMatcher: EMatcher, specificEClasses: List<EClassId> = emptyList()): List<Expr> {
    // The goal of eMatch is to find all the expressions that match the input expression and return them as a list
    // I am visualising this as zipping the incoming expression onto all top level expressions that are represented in the egraph.
    // TODO: should we also match sub expressions. Commutativity matches `a+b` and `(...)+c` in the expression `(a+b)+c`
    // I believe that the way this is being done below will actually account for the subexpressions since it starts searching through all eclasses

    // the algorithm will be something along the lines of
    // 1. what is the root element in the expression? Find all eClasses that contain it.
    // 2. find all enodes in the eclasses that match the element, including matching number of children
    // 3. For each matching enode, ematch each child expr against the eclass that is the operand of the enode, in order of children.
    // 3.1 If all children match, then create an expression from the results returned in the child ematching and add it to the output list
    // 4. If the expr has no children then it is terminal and any matches will be added to the output list
    // 5. If no enodes match then an empty list is returned.
    // Finally, return the output list

    val nodesToSearch =
        if (specificEClasses.isNotEmpty()) specificEClasses.mapNotNull { eGraph.eClassesById[it] }.flatMap { it.nodes }
        else eGraph.eNodes

    val allMatchedExpressions = nodesToSearch
        .filter { identifierMatch(it, eMatcher) }
        .filter { it.children.size == eMatcher.children.size }
        .flatMap { eNode ->
            if (eNode.children.isEmpty()) return@flatMap listOf(eGraph.builder.build(eNode))

            val allMatchedExpressionsForEachChild = eNode.children.zip(eMatcher.children)
                .map { (child, childMatcher) -> eMatch(eGraph, childMatcher, listOf(child)) }

            val firstChildMatchedExpressions = allMatchedExpressionsForEachChild.first()
            val cartesianProductStarter = firstChildMatchedExpressions.map(::listOf)

            val allPossibleChildCombos = allMatchedExpressionsForEachChild.drop(1)
                .fold(cartesianProductStarter) { acc, childMatchedExpressions ->
                    val output = mutableListOf<List<Expr>>()

                    acc.forEach { childrenCombo ->
                        childMatchedExpressions.forEach { childExpr ->
                            output += childrenCombo + childExpr
                        }
                    }

                    return@fold output
                }

            return@flatMap allPossibleChildCombos.map { childCombo -> eGraph.builder.build(eNode, childCombo) }
        }

    return allMatchedExpressions
}

fun identifierMatch(eNode: ENode, eMatcher: EMatcher): Boolean {
    return when (eMatcher) {
        is AnyNode -> true
        is AnyConst -> eNode is EConst
        is AnyVar -> eNode is EVar
        is ConstMatcher -> eNode.identifier == eMatcher.value.toString()
        is VarMatcher -> eNode.identifier == eMatcher.name
        is BinaryMatcher -> eNode.identifier == eMatcher.operation.symbol
    }
}

fun Expr.toEMatcher(): EMatcher {
    return when (this) {
        is Const -> ConstMatcher(value)
        is Var -> VarMatcher(name)
        is BinaryExpr -> BinaryMatcher(left.toEMatcher(), operation, right.toEMatcher())
        else -> TODO("EMatcher mapping for $javaClass not supported yet")
    }
}

sealed class EMatcher(val children: List<EMatcher> = emptyList()) {}
data object AnyVar: EMatcher()
data object AnyConst: EMatcher()
data object AnyNode: EMatcher()
data class ConstMatcher(val value: Double) : EMatcher()
data class VarMatcher(val name: String) : EMatcher()
data class BinaryMatcher(val left: EMatcher, val operation: Operation, val right: EMatcher) : EMatcher(listOf(left, right))


fun main() {
    val egg = MathsEGraph()

    egg.add("x".v + 1.c)

    val rewriteRules = listOf(
        RewriteRule(BinaryMatcher(AnyNode, Operation.ADD, AnyNode)) {
            if (it !is BinaryExpr || it.operation != ADD) null
            else Add(it.right, it.left)
        },
        RewriteRule(BinaryMatcher(BinaryMatcher(AnyNode, ADD, AnyNode), ADD, AnyNode)) {
            if (it !is BinaryExpr || it.operation != ADD) null
            else if (it.left !is BinaryExpr || it.left.operation != ADD) null
            else Add(it.left.left, Add(it.left.right, it.right))
        }
    )
    saturate(egg, rewriteRules)

    println(eMatch(egg, 1.c))
    println(eMatch(egg, "x".v + 1.c))
    println(eMatch(egg, 1.c + "x".v))
    println(eMatch(egg, BinaryMatcher(AnyNode, ADD, AnyNode)))

    println("Associativity")

    egg.add("a".v + "b".v + "c".v)
    println(eMatch(egg, "a".v + "b".v + "c".v))

    saturate(egg, rewriteRules)
    println(eMatch(egg, "a".v + ("b".v + "c".v)))

}

class RewriteRule(val structure: EMatcher, val rewrite: (Expr) -> Expr?)
