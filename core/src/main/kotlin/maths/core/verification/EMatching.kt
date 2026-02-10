package maths.core.verification

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

fun <ExprType> EGraph<ExprType>.eMatch(eMatcher: EMatcher, nodesToSearch: List<ENode> = eNodes): List<EMatchResult> {
    return nodesToSearch
        .filter { eMatcher idMatches it.identifier }
        .filter { eMatcher childrenCountMatches it.childEClasses.size }
        .flatMap { getMatchResults(eMatcher, it) }
}

fun <ExprType> EGraph<ExprType>.getMatchResults(matcher: EMatcher, node: ENode): List<EMatchResult> {
    val nodeEClass = eNodeHashCons[node.toHashKey] ?: return emptyList()

    return when (matcher) {
        is AnyNode -> listOf(EMatchResult(nodeEClass, mapOf(matcher.name to nodeEClass)))
        is ConstMatcher -> listOf(EMatchResult(nodeEClass))
        is UnaryMatcher -> {
            val operandEClass = node.childEClasses[0]

            val operandResults = eMatch(matcher.operand, operandEClass.nodes).ifEmpty { return emptyList() }

            operandResults.map { EMatchResult(nodeEClass, it.matchedGroups) }
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

infix fun List<EMatchResult>.combineWith(otherList: List<EMatchResult>): List<Map<String, EClass>> {
    return flatMap { first -> otherList.map { second -> first to second } }
        .filter { consistentMatches(it.first.matchedGroups, it.second.matchedGroups) }
        .map { it.first.matchedGroups + it.second.matchedGroups }
}

fun consistentMatches(firstMap: Map<String, EClass>, secondMap: Map<String, EClass>): Boolean {
    return secondMap.all { (matchName, eClass) ->
        eClass == (firstMap[matchName] ?: eClass)
    }
}