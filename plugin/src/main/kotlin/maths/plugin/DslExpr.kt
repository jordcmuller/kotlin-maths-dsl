package maths.plugin

import maths.core.ast.*
import org.jetbrains.kotlin.psi.*

val operationMap: Map<String, Operation> = mapOf(
    Operation.ADD.symbol to Operation.ADD,
    Operation.SUB.symbol to Operation.SUB,
    Operation.MUL.symbol to Operation.MUL,
    Operation.DIV.symbol to Operation.DIV,
)

object DslPsiParser {

    fun parseStatement(element: KtElement): Stmt? {
        return when (element) {
            is KtProperty -> parseVariablePropertyDelegate(element)
            is KtBinaryExpression -> parseEquality(element)
            else -> null
        }
    }

    private fun parseVariablePropertyDelegate(property: KtProperty): Stmt? {
        val name = property.name ?: return null

        property.delegateExpressionOrInitializer?.let { delegate ->
            val ref = delegate as? KtNameReferenceExpression ?: return null
            if (ref.name != "variable $name") return null
            return VariableDeclaration(name)
        }

        return null
    }

    private fun parseEquality(binary: KtBinaryExpression): Stmt? {
        val op = binary.operationReference.getReferencedName()
        if (op == "eq") {
            val left = binary.left?.let { parse(it) } ?: return null
            val right = binary.right?.let { parse(it) } ?: return null
            return Equation(left, right)
        }
        return null
    }

    fun parse(expr: KtExpression?): Expr? = when (expr) {
        null -> null

        // Classic form: binary expressions (K1)
        is KtBinaryExpression -> {
            val op = operationMap[expr.operationReference.text] ?: return null
            val left = parse(expr.left)
            val right = parse(expr.right)
            if (left != null && right != null) BinaryExpr(left, op, right) else null
        }

        // K2 form: desugared operator call like `x.plus(2.0)`
        is KtCallExpression -> {
            val callee = expr.calleeExpression?.text ?: return null
            val args = expr.valueArguments.mapNotNull { parse(it.getArgumentExpression()) }

            when {
                callee in setOf("plus", "minus", "times", "div") && args.size == 1 -> {
                    // Receiver is the "left" side
                    val left = (expr.parent as? KtDotQualifiedExpression)?.receiverExpression?.let { parse(it) }
                    val right = args.single()
                    if (left != null) {
                        val symbol = when (callee) {
                            "plus" -> "+"
                            "minus" -> "-"
                            "times" -> "*"
                            "div" -> "/"
                            else -> callee
                        }
                        val operation = operationMap[symbol] ?: return null
                        BinaryExpr(left, operation, right)
                    } else null
                }
                else -> null
            }
        }

        // Variables
        is KtNameReferenceExpression -> Var(expr.getReferencedName())

        // Constants
        is KtConstantExpression -> expr.text.toDoubleOrNull()?.let { Const(it) }

        // Dot-qualified receiver (needed for K2 operator calls)
        is KtDotQualifiedExpression -> {
            val receiver = parse(expr.receiverExpression)
            val selector = expr.selectorExpression?.text

            if (receiver is Const && selector == "c") {
                // Turn 2.c into Const(2)
                receiver
            } else {
                null // could extend later for more DSL props
            }
        }

        else -> null
    }
}
