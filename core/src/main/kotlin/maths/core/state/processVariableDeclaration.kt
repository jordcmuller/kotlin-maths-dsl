package maths.core.state

import maths.core.ast.VariableDeclaration

fun MathsState.processVariableDeclaration(variableDeclaration: VariableDeclaration) {
    statements += variableDeclaration

    if (variableDeclaration.variable.isDeclared) {
        errors.add(ValidationError(variableDeclaration, "Variable '${variableDeclaration.variable.name}' is already defined"))
    } else {
        variableDeclaration.declare()
    }
}
