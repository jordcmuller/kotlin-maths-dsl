package maths.core.verification

fun <ExprType> EGraph<ExprType>.dump(): String = buildString {
    appendLine("EGraph")
    eClasses.forEach {
        appendLine("    EClass ${it.id}")
        it.nodes.forEach { node ->
            appendLine("        ${pretty(node)}")
        }
    }
}.trimIndent()

private fun pretty(node: ENode): String = buildString {
    append(node.identifier)
    if (node.children.isNotEmpty()) {
        append("(${node.children.joinToString(" ")})")
    }
}
