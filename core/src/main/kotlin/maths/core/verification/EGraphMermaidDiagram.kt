package maths.core.verification

fun <ExprType> EGraph<ExprType>.toMermaid(): String {
    val diagramSections = listOf(
        "graph TD",
        generateEClassSubgraphs(),
        generateAllENodeLinks(),
    )

    return diagramSections
        .filter { it.isNotEmpty() }
        .joinToString("\n\n")
}

private fun <ExprType> EGraph<ExprType>.generateEClassSubgraphs(): String {
    return eClasses.joinToString("\n\n") { it.toMermaidSubgraph().prependIndent() }
}

private fun EClass.toMermaidSubgraph() = buildString {
    val clusterId = "EClass$id"

    appendLine("subgraph $clusterId[\"$id\"]")
    appendLine("    style $clusterId stroke-dasharray: 5 5")

    nodes.forEachIndexed { index, node ->
        val nodeId = eNodeId(id, index)
        appendLine("    $nodeId[\"${node.identifier}\"]")
    }

    append("end")
}

private fun <ExprType> EGraph<ExprType>.generateAllENodeLinks(): String {
    return eClasses
        .map { it.generateENodeOperandLinks() }
        .filter { it.isNotEmpty() }
        .joinToString("\n") { it.prependIndent() }
}

private fun EClass.generateENodeOperandLinks() = buildString {
    nodes.forEachIndexed { index, node ->
        val fromENode = eNodeId(id, index)
        append(node.children.joinToString("\n") { childId -> "$fromENode --> EClass$childId" })
    }
}

private fun eNodeId(eClassId: EClassId, index: Int): String =
    "E${eClassId}_N$index"
