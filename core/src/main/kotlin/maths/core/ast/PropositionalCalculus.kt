package maths.core.ast

interface Proposition: Stmt

interface PropositionAtom: Proposition
class BasicEquation: PropositionAtom

data class And(val left: Proposition, val right: Proposition): Proposition
data class Or(val left: Proposition, val right: Proposition): Proposition
data class Not(val proposition: Proposition): Proposition
data class Implies(val left: Proposition, val right: Proposition): Proposition

infix fun Proposition.and(that: Proposition): Proposition = And(this, that)
infix fun Proposition.or(that: Proposition): Proposition = Or(this, that)
infix fun Proposition.implies(that: Proposition): Proposition = Implies(this, that)

operator fun Proposition.not(): Proposition = Not(this)

fun main() {
    val a = BasicEquation()
    val b = BasicEquation()

    val myImpliedProposition = !(!a implies !(b or a))
}