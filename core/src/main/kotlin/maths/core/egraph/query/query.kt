package maths.core.egraph.query

import maths.core.egraph.AnyNode
import maths.core.egraph.BinaryMatcher
import maths.core.egraph.ConstMatcher
import maths.core.egraph.EGraph
import maths.core.egraph.EMatchResult
import maths.core.egraph.EMatcher
import maths.core.egraph.UnaryMatcher
import maths.core.egraph.VarMatcher
import maths.core.egraph.eMatch
import maths.core.egraph.toExactEMatcher
import maths.core.rewriting.eMatcherToExpr

fun EGraph.query(queryBuilderFunc: QueryBuilder.() -> Unit): List<EMatchResult> {
    val graphQuery = QueryBuilder().apply(queryBuilderFunc).build()
    return query(graphQuery)
}

fun EGraph.query(query: GraphQuery):List<EMatchResult> {
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
