package maths.core.verification

fun EGraph.toMermaid(): String {
    val diagramSections = listOf(
        "graph TD",
        generateEClassSubgraphs(),
        generateAllENodeLinks(),
    )

    return diagramSections
        .filter { it.isNotEmpty() }
        .joinToString("\n\n")
}

private fun EGraph.generateEClassSubgraphs(): String {
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

private fun EGraph.generateAllENodeLinks(): String {
    return eClasses
        .map { it.generateENodeOperandLinks() }
        .filter { it.isNotEmpty() }
        .joinToString("\n") { it.prependIndent() }
}

private fun EClass.generateENodeOperandLinks() = buildString {
    nodes.forEachIndexed { index, node ->
        val fromENode = eNodeId(id, index)
        append(node.childEClasses.joinToString("\n") { childId -> "$fromENode --> EClass$childId" })
    }
}

private fun eNodeId(eClassId: Int, index: Int): String =
    "E${eClassId}_N$index"
