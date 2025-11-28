package maths.core.ast

import kotlin.reflect.KProperty

enum class Operation(val symbol: String) {
    ADD("+"),
    MUL("*"),
    SUB("-"),
    DIV("/")
}

// ---------------------------
// AST
// ---------------------------
sealed interface Expr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Var {
        return Var(property.name)
    }
}

//@JvmInline
open class Const(val value: Double) : Expr

class Var(val name: String) : Expr

open class BinaryExpr(val left: Expr, val operation: Operation, val right: Expr) : Expr

class Add(left: Expr, right: Expr) : BinaryExpr(left, Operation.ADD, right)

class Sub(left: Expr, right: Expr) : BinaryExpr(left, Operation.SUB, right)

class Mul(left: Expr, right: Expr) : BinaryExpr(left, Operation.MUL, right)

class Div(left: Expr, right: Expr) : BinaryExpr(left, Operation.DIV, right)

class Pow(val base: Expr, val exp: Expr) : Expr

class Neg(val child: Expr) : Expr

class Func(val name: String, val arg: Expr) : Expr


sealed interface Stmt
// ---------------------------
// Equation
// ---------------------------
class Equation(val left: Expr, val right: Expr) : Stmt {
    fun normalized(): Expr = Sub(left, right) // canonicalize to single expression for equivalence checks
}

class VariableDeclaration(val name: String) : Stmt
