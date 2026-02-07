package maths.core.verification

import maths.core.ast.Expr

infix fun EMatcher.idMatches(identifier: String) = when (this) {
    is AnyNode -> true
    is BinaryMatcher -> operations.any { it.symbol == identifier }
    else -> TODO("idMatches not implemented yet for $this")
}

infix fun EMatcher.childrenCountMatches(size: Int) = when (this) {
    is AnyNode -> true
    else -> children.size == size
}

fun EGraph<Expr>.eMatch(eMatcher: EMatcher, nodesToSearch: List<ENode> = eNodes): List<EMatchResult> {
    return nodesToSearch
        .filter { eMatcher idMatches it.identifier }
        .filter { eMatcher childrenCountMatches it.children.size }
        .flatMap { getMatchResults(eMatcher, it) }
}

fun EGraph<Expr>.getMatchResults(matcher: EMatcher, node: ENode): List<EMatchResult> {
    val nodeEClassId = eNodeHashCons[node.toHashKey] ?: return emptyList()

    return when (matcher) {
        is AnyNode -> listOf(EMatchResult(nodeEClassId, mapOf(matcher.name to nodeEClassId)))
        is BinaryMatcher -> {
            val leftEClass = eClassesById[node.children[0]] ?: return emptyList()
            val rightEClass = eClassesById[node.children[1]] ?: return emptyList()

            val leftResults = eMatch(matcher.left, leftEClass.nodes).ifEmpty { return emptyList() }
            val rightResults = eMatch(matcher.right, rightEClass.nodes).ifEmpty { return emptyList() }

            val combinedConsistentMatches = leftResults combineWith rightResults
            combinedConsistentMatches.map { EMatchResult(nodeEClassId, it) }
        }
        else -> TODO("getMatchResults not implemented yet for $matcher")
    }
}

infix fun List<EMatchResult>.combineWith(otherList: List<EMatchResult>): List<Map<String, EClassId>> {
    return flatMap { first -> otherList.map { second -> first to second } }
        .filter { consistentMatches(it.first.matchedGroups, it.second.matchedGroups) }
        .map { it.first.matchedGroups + it.second.matchedGroups }
}

fun consistentMatches(firstMap: Map<String, EClassId>, secondMap: Map<String, EClassId>): Boolean {
    return secondMap.all { (matchName, eClassId) ->
        firstMap.containsKey(matchName) && firstMap[matchName] != eClassId
    }
}