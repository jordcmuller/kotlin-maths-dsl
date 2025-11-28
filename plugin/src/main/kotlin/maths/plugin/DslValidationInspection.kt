package maths.plugin

import com.intellij.codeInspection.*
import maths.core.dsl.MathsContext
import org.jetbrains.kotlin.idea.codeinsight.api.classic.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.*

class DslValidationInspection : AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)

                val calleeName = expression.calleeExpression?.text
                if (calleeName != "maths") return

                // Parse block argument(s)
                val lambdaArg = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()
                val body = lambdaArg?.bodyExpression ?: return

                // Walk through its body block sequentially
                val context = MathsContext()
                for (stmt in body.statements) {
                    when (stmt) {
                        is KtProperty -> {
                            val name = stmt.name ?: continue
                            context.declareVar(name)

                            // Case 2: val x by <delegate>
                            stmt.delegate?.expression?.let { delegateExpr ->
                                val dslExpr = DslPsiParser.parseStatement(delegateExpr)
                                if (dslExpr != null) {
                                    context.validateStatement(dslExpr).forEach { error ->
                                        holder.registerProblem(delegateExpr, error.message)
                                    }
                                }
                            }
                        }

                        // case: bare expression statement
                        is KtExpression -> {
                            val dslExpr = DslPsiParser.parseStatement(stmt) ?: continue
                            val errors = context.validateStatement(dslExpr)
                            errors.forEach { error ->
                                holder.registerProblem(stmt, error.message)
                            }
                        }
                    }
                }
            }
        }
    }
}