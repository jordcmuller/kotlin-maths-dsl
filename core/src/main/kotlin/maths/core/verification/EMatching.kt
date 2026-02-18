package maths.core.verification

import maths.core.ast.Expr
import maths.core.verification.extraction.canonicalForm

infix fun EMatcher.idMatches(identifier: String) = when (this) {
    is AnyNode -> true
    is ConstMatcher -> value.toString() == identifier
    is UnaryMatcher -> operation.symbol == identifier
    is BinaryMatcher -> operation.symbol == identifier
    else -> TODO("idMatches not implemented yet for $this")
}

infix fun EMatcher.childrenCountMatches(size: Int) = when (this) {
    is AnyNode -> true
    else -> children.size == size
}

fun EGraph.eMatch(eMatcher: EMatcher, nodesToSearch: List<ENode> = eNodes): List<EMatchResult> {
    return nodesToSearch
        .filter { eMatcher idMatches it.identifier }
        .filter { eMatcher childrenCountMatches it.childEClasses.size }
        .flatMap { getMatchResults(eMatcher, it) }
}

fun EGraph.getMatchResults(matcher: EMatcher, node: ENode): List<EMatchResult> {
    val nodeEClass = eNodeHashCons[node.toHashKey] ?: return emptyList()
    val nodeCanonicalForm = nodeEClass.canonicalForm

    return when (matcher) {
        is AnyNode -> listOf(EMatchResult(nodeEClass, mapOf(matcher.name to nodeCanonicalForm)))
        is ConstMatcher -> listOf(EMatchResult(nodeEClass))
        is UnaryMatcher -> {
            val operandEClass = node.childEClasses[0]

            val operandResults = eMatch(matcher.operand, operandEClass.nodes).ifEmpty { return emptyList() }

            operandResults.map { EMatchResult(nodeEClass, it.matchedExpressions) }
        }
        is BinaryMatcher -> {
            val leftEClass = node.childEClasses[0]
            val rightEClass = node.childEClasses[1]

            val leftResults = eMatch(matcher.left, leftEClass.nodes).ifEmpty { return emptyList() }
            val rightResults = eMatch(matcher.right, rightEClass.nodes).ifEmpty { return emptyList() }

            val combinedConsistentMatches = leftResults combineWith rightResults
            combinedConsistentMatches.map { EMatchResult(nodeEClass, it) }
        }
        else -> TODO("getMatchResults not implemented yet for $matcher")
    }
}

infix fun List<EMatchResult>.combineWith(otherList: List<EMatchResult>): List<Map<String, Expr>> {
    return flatMap { first -> otherList.map { second -> first to second } }
        .filter { consistentMatches(it.first.matchedExpressions, it.second.matchedExpressions) }
        .map { it.first.matchedExpressions + it.second.matchedExpressions }
}

fun consistentMatches(firstMap: Map<String, Expr>, secondMap: Map<String, Expr>): Boolean {
    return secondMap.all { (matchName, eClass) ->
        eClass == (firstMap[matchName] ?: eClass)
    }
}