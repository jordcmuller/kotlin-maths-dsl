package maths.core.state

import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Stmt
import maths.core.ast.Var
import maths.core.ast.VariableDeclaration
import maths.core.format.readable
import maths.core.verification.EquivalenceManager

class MathsState {

    // Variables
    val definedVars = mutableSetOf<String>()
    val variableValues = mutableMapOf<String, Const>()

    /** Called when a new variable is declared */
    fun VariableDeclaration.declare() {
        definedVars.plusAssign(variable.name)
        equivalenceManager.declareVariable(variable)
    }
    val Var.isDeclared get() = definedVars.contains(name)
    fun Var.set(const: Const) = variableValues.put(name, const).also { value = const }

    val Var.isKnown get() = variableValues.containsKey(name)
    val Var.isUnknown get() = !isKnown

    // History
    val statements = mutableListOf<Stmt>()
    val errors = mutableListOf<ValidationError>()

    // Fancy stuff
    val representations = mutableMapOf<String, MutableList<Expr>>()
    val equivalenceManager = EquivalenceManager()


//================================================================================================================

    /** if the expression is a variable then we resolve whatever it is attached to
     * if the variable is not linked to any other expressions or values then null is returned
     * if the expression is not a variable then we return the expression
     * */

//    fun getBindings(variable: Var): MutableSet<Expr> {
//        return mutableSetOf<Expr>().also { resolveBindingsInContext(variable, it) }
//    }
//
//    fun resolveBindingsInContext(variable: Var, bindingSet: MutableSet<Expr>) {
//        val bindings = representations[variable.name] ?: return
//        for (binding in bindings) {
//            bindingSet.add(binding)
//            if (binding is Var) resolveBindingsInContext(binding, bindingSet)
//        }
//    }
//
//    fun bind(name: String, expr: Expr) {
//        // TODO: update this to include all expressions not just variables
//        representations.getOrPut(name) { mutableListOf() }.add(expr)
//        if (expr is Const) { variableValues[name] = expr }
//    }
}

/** A simple error container */
class ValidationError(val statement: Stmt, val message: String) {
    override fun toString(): String {
        return "'${statement.readable()}' is invalid due to '$message'"
    }
}
