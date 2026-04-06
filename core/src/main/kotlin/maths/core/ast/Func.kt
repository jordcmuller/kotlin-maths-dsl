package maths.core.ast

class Func(val name: String, val params: List<Var>, val functionBody: Expr) : Expr {
    override fun toString(): String {
        return "$name(${params.joinToString(", ")}) = $functionBody"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Func

        if (name != other.name) return false
        if (params != other.params) return false
        if (functionBody != other.functionBody) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + functionBody.hashCode()
        return result
    }
}

fun Func.derived() {
    TODO("How to derive a function with limits")
}

class Limit(val variable: Var, val approaching: Expr, val expression: Expr): Expr

fun evaluate() {
    TODO("Not yet implemented")
}
