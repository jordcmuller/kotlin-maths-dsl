package maths.core.ast

enum class Operation(val symbol: String) {
    ADD("+"),
    MUL("*"),
    SUB("-"),
    DIV("/")
}

//@JvmInline
data class Const(val value: Double) : Expr

data class Var(val name: String) : Expr {
    var value: Expr? = null
}

open class BinaryExpr(val left: Expr, val operation: Operation, val right: Expr) : Expr {
    override fun equals(other: Any?): Boolean {
        if (other !is BinaryExpr) return false
        if (operation != other.operation) return false
        if (left != other.left || right != other.right) return false
        return true
    }

    override fun hashCode(): Int {
        return left.hashCode() + "+".hashCode() + right.hashCode()
    }
}

class Add(left: Expr, right: Expr) : BinaryExpr(left, Operation.ADD, right)

class Sub(left: Expr, right: Expr) : BinaryExpr(left, Operation.SUB, right)

class Mul(left: Expr, right: Expr) : BinaryExpr(left, Operation.MUL, right)

class Div(left: Expr, right: Expr) : BinaryExpr(left, Operation.DIV, right)

class Pow(val base: Expr, val exp: Expr) : Expr

class Neg(val child: Expr) : Expr

class Func(val name: String, val arg: Expr) : Expr


interface Stmt

data class Equation(val left: Expr, val right: Expr) : Proposition {
    var equivalence = Equivalence.Unknown
    fun normalized(): Expr = Sub(left, right) // canonicalize to single expression for equivalence checks
}

data class VariableDeclaration(val name: String) : Stmt {
    val variable = Var(name)
}
