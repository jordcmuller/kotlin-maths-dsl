package maths.core.verification

import maths.core.ast.Expr
import maths.core.rewriting.AndCondition
import maths.core.rewriting.EqualsCondition
import maths.core.rewriting.GraphQuery
import maths.core.rewriting.NotEqualsCondition
import maths.core.rewriting.OrCondition
import maths.core.rewriting.PatternCondition
import maths.core.rewriting.QueryCondition
import maths.core.rewriting.eMatcherToExpr
import maths.core.verification.extraction.canonicalForm

fun EGraph.processQuery(query: GraphQuery):List<EMatchResult> {
    var results = listOf(EMatchResult())

    for (premise in query.premises) {
        results = handleCondition(premise, results)
    }

    return results
}

private fun EGraph.handleCondition(currentPremise: QueryCondition, currentResults: List<EMatchResult>): List<EMatchResult> {
    return when (currentPremise) {
        is PatternCondition -> {
            currentResults.flatMap { currentResult ->
                eMatch(currentPremise.pattern.fillInKnownVariables(currentResult))
                    .map { EMatchResult(currentResult.matchedExpressions + it.matchedExpressions) }
            }
        }
        is EqualsCondition -> {
            val leftResults = handleCondition(currentPremise.left, currentResults)
            val rightResults = handleCondition(currentPremise.right, leftResults)

            val equalResults = rightResults.filter { result ->
                val leftExpr = currentPremise.left.pattern.eMatcherToExpr(result)
                val leftEClass = add(leftExpr)
                val rightExpr = currentPremise.right.pattern.eMatcherToExpr(result)
                val rightEClass = add(rightExpr)
                leftEClass == rightEClass
            }

            equalResults
        }
        is NotEqualsCondition -> {
            val leftResults = handleCondition(currentPremise.left, currentResults)
            val rightResults = handleCondition(currentPremise.right, leftResults)

            val equalResults = rightResults.filter { result ->
                val leftExpr = currentPremise.left.pattern.eMatcherToExpr(result)
                val leftEClass = add(leftExpr)
                val rightExpr = currentPremise.right.pattern.eMatcherToExpr(result)
                val rightEClass = add(rightExpr)
                leftEClass != rightEClass
            }

            equalResults
        }
        is AndCondition -> {
            val firstResults = handleCondition(currentPremise.left, currentResults)

            val secondResults = handleCondition(currentPremise.right, firstResults)

            secondResults
        }
        is OrCondition -> {
            val firstBranchResults = handleCondition(currentPremise.left, currentResults)
            val secondBranchResults = handleCondition(currentPremise.right, currentResults)

            firstBranchResults + secondBranchResults
        }
        else -> TODO()
    }
}

fun EMatcher.fillInKnownVariables(result: EMatchResult): EMatcher = when (this) {
    is AnyNode -> result[this]?.toExactEMatcher() ?: this
    is ConstMatcher, is VarMatcher -> this
    is UnaryMatcher -> UnaryMatcher(operation, operand.fillInKnownVariables(result))
    is BinaryMatcher -> BinaryMatcher(left.fillInKnownVariables(result), operation, right.fillInKnownVariables(result))
    else -> TODO()
}

fun EGraph.eMatch(eMatcher: EMatcher, nodesToSearch: List<ENode> = eNodes): List<EMatchResult> {
    if (eMatcher is AnyNode)
        return nodesToSearch
            .map { it.parentEClass }.distinct()
            .map { EMatchResult(mapOf(eMatcher.name to it.canonicalForm)) }

    return nodesToSearch
        .filter { eMatcher idMatches it.identifier }
        .filter { eMatcher childrenCountMatches it.childEClasses.size }
        .flatMap { getMatchResults(eMatcher, it) }
}

infix fun EMatcher.idMatches(identifier: String) = when (this) {
    is AnyNode -> true
    is ConstMatcher -> value.toString() == identifier
    is VarMatcher -> name == identifier
    is UnaryMatcher -> operation.symbol == identifier
    is BinaryMatcher -> operation.symbol == identifier
    else -> TODO("idMatches not implemented yet for $this")
}

infix fun EMatcher.childrenCountMatches(size: Int) = when (this) {
    is AnyNode -> true
    else -> children.size == size
}

fun EGraph.getMatchResults(matcher: EMatcher, node: ENode): List<EMatchResult> {
    val nodeEClass = eNodeHashCons[node.toHashKey] ?: return emptyList()
    val nodeCanonicalForm = nodeEClass.canonicalForm

    return when (matcher) {
        is AnyNode -> listOf(EMatchResult(mapOf(matcher.name to nodeCanonicalForm)))
        is ConstMatcher, is VarMatcher -> listOf(EMatchResult())
        is UnaryMatcher -> {
            val operandEClass = node.childEClasses[0]

            eMatch(matcher.operand, operandEClass.nodes).ifEmpty { return emptyList() }
        }
        is BinaryMatcher -> {
            val leftEClass = node.childEClasses[0]
            val rightEClass = node.childEClasses[1]

            val leftResults = eMatch(matcher.left, leftEClass.nodes).ifEmpty { return emptyList() }
            val rightResults = eMatch(matcher.right, rightEClass.nodes).ifEmpty { return emptyList() }

            val combinedConsistentMatches = leftResults combineWith rightResults
            combinedConsistentMatches.map { EMatchResult(it) }
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