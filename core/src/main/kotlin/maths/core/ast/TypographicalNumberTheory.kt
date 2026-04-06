package maths.core.ast

import maths.patterns.ExprPattern

enum class Operation(val symbol: String) {
    ADD("+"),
    MUL("*"),
    SUB("-"),
    DIV("/"),
    POW("^");

    companion object {
        fun fromString(symbol: String) = entries.first { it.symbol == symbol }
    }
}

//@JvmInline
data class Const(val value: Int) : Expr

val zero = object: Expr {
    override fun toString() = "0"
}

class Successor(val of: Expr): Expr {
    override fun toString() = "S$of"
}

data class Var(val name: String) : Expr {
    var value: Expr? = null
}

open class BinaryExpr(val left: Expr, val operation: Operation, val right: Expr) : Expr {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is BinaryExpr) return false
        if (this === other) return true

        return ExprPattern.fromExpr(this).accepts(other)
    }

    override fun hashCode(): Int {
        return left.hashCode() + operation.symbol.hashCode() + right.hashCode()
    }

    override fun toString() = "($left ${operation.symbol} $right)"
}

class Add(left: Expr, right: Expr) : BinaryExpr(left, Operation.ADD, right)

class Sub(left: Expr, right: Expr) : BinaryExpr(left, Operation.SUB, right)

class Mul(left: Expr, right: Expr) : BinaryExpr(left, Operation.MUL, right)

class Div(left: Expr, right: Expr) : BinaryExpr(left, Operation.DIV, right)

class Pow(val base: Expr, val exp: Expr) : BinaryExpr(base, Operation.POW, exp)

open class UnaryExpr(val operation: Operation, val operand: Expr) : Expr {
    override fun toString() = "$operand$operation"
}

class Neg(val child: Expr) : UnaryExpr(SUB, child)


interface Stmt

data class Equation(val left: Expr, val right: Expr) : Proposition {
    var equivalence = Equivalence.Unknown
    fun normalized(): Expr = Sub(left, right) // canonicalize to single expression for equivalence checks
}

data class VariableDeclaration(val name: String) : Stmt {
    val variable = Var(name)
}

fun S(`_`: Expr) = Successor(`_`)

val Expr.s: Expr get() = S(this)

fun Nat(int: Int): Expr = when (int) {
    0 -> zero
    else -> Successor(Nat(int - 1))
}

val Int.nat: Expr get() = Nat(this)

val Expr.isNat: Boolean get() = when (this) {
    zero -> true
    is Successor -> this.of.isNat
    else -> false
}

fun main() {
    val one = S(zero)
    val two = S(one)
    val three = S(two)

    println("Zero: $zero")
    println("One: $one")
    println("Two: $two")
    println("Three: $three")
}
