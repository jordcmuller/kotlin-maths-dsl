package maths.core.verification

import maths.core.ast.Expr

fun <ExprType: Expr> EGraph<ExprType>.print(): String = buildString {
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
    if (node.childEClasses.isNotEmpty()) {
        append("(${node.childEClasses.joinToString(" ")})")
    }
}
