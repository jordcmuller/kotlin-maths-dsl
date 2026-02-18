package maths.core.dsl

import maths.core.ast.BinaryExpr
import maths.core.ast.Const
import maths.core.ast.Expr
import maths.core.ast.Func
import maths.core.ast.UnaryExpr
import maths.core.ast.Var
import kotlin.reflect.KProperty

class FunctionDelegate(val params: List<Var>, val function: () -> Expr) {
    lateinit var func: Func

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = func

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): FunctionDelegate {
        val functionBody = function()

        with(params.map { it.name }.toSet()) {
            require(functionBody.isWellDefined())
        }

        func = Func(property.name, params, functionBody)
        return this
    }
}

fun function(vararg params: Var, function: () -> Expr) = FunctionDelegate(params.toList(), function)

context(params: Set<String>)
fun Expr.isWellDefined(): Boolean = when (this) {
    is Var -> params.contains(name)
    is BinaryExpr -> left.isWellDefined() && right.isWellDefined()
    is UnaryExpr -> operand.isWellDefined()
    is Const -> true
    else -> TODO("Not implemented yet for $javaClass")
}