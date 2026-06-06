package maths.core.egraph

import maths.core.ast.*
import kotlin.reflect.KClass

class ExprLowerer {

    lateinit var eGraph: EGraph
    val loweringFunctions = mutableMapOf<KClass<*>, (EGraph, Any) -> Any>()

    fun <T1 : Any> get(item1: T1): ((EGraph, T1) -> ENode)? {
        val func = loweringFunctions[item1::class] ?: return null
        return func as (EGraph, T1) -> ENode
    }

    fun lower(expr: Expr, add: (ENode) -> EClass): EClass {

        val dynamicFunc = get(expr)
        if (dynamicFunc != null) {
            return add(dynamicFunc(eGraph, expr))
        }

        return when (expr) {
            is Const -> add(EConst(expr.value))

            is Var -> add(EVar(expr.name))

            is BinaryExpr -> {
                val l = lower(expr.left, add)
                val r = lower(expr.right, add)
                add(EBinary(l, expr.operation.symbol, r))
            }

            is UnaryExpr -> {
                val operand = lower(expr.operand, add)
                add(EUnary(expr.operation.symbol, operand))
            }

            is Func -> {
                TODO("Scope the function parameters to the function name like f.x, f.y")
                // Since we are scoping the params, does it not make more sense
                // to create a new e-graph and have that act as a separate scope?
            }

            else -> error("Unknown expression")
        }
    }
}

