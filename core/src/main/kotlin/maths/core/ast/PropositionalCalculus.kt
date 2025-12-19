package maths.core.ast

import maths.core.dsl.MathsContext
import maths.core.dsl.maths

interface Proposition: Stmt

class BasicEquation: Proposition

data class And(val left: Proposition, val right: Proposition): Proposition
data class Or(val left: Proposition, val right: Proposition): Proposition
data class Not(val proposition: Proposition): Proposition
data class Implies(val left: Proposition, val right: Proposition): Proposition

context(_: MathsContext) infix fun Proposition.and(that: Proposition) = And(this, that)
context(_: MathsContext) infix fun Proposition.or(that: Proposition) = Or(this, that)
context(_: MathsContext) infix fun Proposition.implies(that: Proposition) = Implies(this, that)

operator fun Proposition.not(): Proposition = Not(this)

interface Quantifier
class ForAll(val variable: Var): Quantifier
class ThereExists(val variable: Var): Quantifier

context(_: MathsContext) infix fun ForAll.itHoldsThat(proposition: Proposition) = Universal(variable, proposition)
context(_: MathsContext) infix fun ForAll.itHoldsThat(propositionBlock: () -> Proposition) = Universal(variable, propositionBlock())
context(_: MathsContext) infix fun ThereExists.suchThat(proposition: Proposition) = Existential(variable, proposition)
context(_: MathsContext) infix fun ThereExists.suchThat(propositionBlock: () -> Proposition) = Existential(variable, propositionBlock())

interface QuantifiedProposition: Proposition
class Universal(val variable: Var, val proposition: Proposition): QuantifiedProposition
class Existential(val variable: Var, val proposition: Proposition): QuantifiedProposition

context(_: MathsContext) fun forall(variable: Var) = ForAll(variable)
context(_: MathsContext) fun thereExists(variable: Var) = ThereExists(variable)

fun main() {
    maths {
        val a by variable()
        val aEqualsItself = a eq a
        forall(a) itHoldsThat { a eq a }
        forall(a) itHoldsThat aEqualsItself

        val b by variable()
        val equalityIsSymmetric = (b eq a) implies (a eq b)
        thereExists(b) suchThat equalityIsSymmetric
        thereExists(b) suchThat { (b eq a) implies (a eq b) }
    }
}