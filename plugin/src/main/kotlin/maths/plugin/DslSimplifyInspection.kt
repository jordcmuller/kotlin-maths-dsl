package maths.plugin

import com.intellij.codeInspection.*
import maths.core.ast.*
import maths.core.format.printKotlin
import org.jetbrains.kotlin.idea.codeinsight.api.classic.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.*

class DslSimplifyInspection : AbstractKotlinInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) =
        object : KtVisitorVoid() {
            override fun visitBinaryExpression(expression: KtBinaryExpression) {
                super.visitBinaryExpression(expression)
                val ast = DslPsiParser.parse(expression) ?: return

                when {
                    // Case 1: x + 0 -> x
                    ast is BinaryExpr &&
                            ast.operation == Operation.ADD &&
                            ast.right is Const &&
                            (ast.right as Const).value == 0.0 -> {
                        holder.registerProblem(
                            expression,
                            "Remove additive identity",
                            SimplifyQuickFix("Remove additive identity", expression.left!!.text)
                        )
                    }

                    // Case 2: x * 1 -> x
                    ast is BinaryExpr &&
                            ast.operation == Operation.MUL &&
                            ast.right is Const &&
                            (ast.right as Const).value == 1.0 -> {
                        holder.registerProblem(
                            expression,
                            "Remove multiplicative identity",
                            SimplifyQuickFix("Remove multiplicative identity", expression.left!!.text)
                        )
                    }

                    // Case 3: reorder commutative (3 + x -> x + 3)
                    ast is BinaryExpr &&
                            ast.operation == Operation.ADD &&
                            ast.left is Const &&
                            ast.right is Var -> {
                        val leftText = ast.left.printKotlin()
                        val rightText = ast.right.printKotlin()
                        holder.registerProblem(
                            expression,
                            "Reorder to $rightText + $leftText",
                            SimplifyQuickFix("Reorder", "$rightText + $leftText")
                        )
                    }

                    // Case 4 (Factorization) – requires deeper pattern matching
                    // (x*2 + x*3) -> x*(2+3)
                    // TODO: implement AST rewrite rules
                }
            }
        }
}

class SimplifyQuickFix(
    private val message: String,
    private val replacement: String
) : LocalQuickFix {
    override fun getFamilyName(): String = "Math DSL QuickFix"

    override fun applyFix(project: com.intellij.openapi.project.Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement as? KtBinaryExpression ?: return
        val factory = org.jetbrains.kotlin.psi.KtPsiFactory(project)
        val newExpr = factory.createExpression(replacement)
        element.replace(newExpr)
    }
}
