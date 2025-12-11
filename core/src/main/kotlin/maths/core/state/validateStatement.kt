package maths.core.state

import maths.core.ast.Equation
import maths.core.ast.Stmt
import maths.core.ast.VariableDeclaration

fun MathsState.validateStatement(stmt: Stmt) = when (stmt) {
    is VariableDeclaration -> processVariableDeclaration(stmt)
    is Equation -> processEquation(stmt)
}
