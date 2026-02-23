package maths.core.rewriting

import maths.core.dsl.c
import maths.core.dsl.div
import maths.core.dsl.minus
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.dsl.unaryMinus
import maths.core.dsl.variable
import maths.core.rewriting.dsl.provideDelegate
import maths.core.rewriting.dsl.rewriteRule
import maths.core.rewriting.dsl.where

private val x by variable()
private val y by variable()
private val z by variable()

val multiplicativeCommutativity by x * y to y * x
val multiplicativeAssociativity by x * y * z to x * (y * z)
val multiplicativeIdentity by x * 1 to x
val multiplicativeInverse by x / y to x * (1 / y)

val additiveCommutativity by x + y to y + x
val additiveAssociativity by x + y + z to x + (y + z)
val additiveIdentity by x + 0 to x
val additiveInverse by x - y to x + (-y)

val distributivity by x * (y + z) to x * y + x * z

/*
* The idea behind the new premise-based/conditional rewrite rules is to have a premise that needs to be fulfilled
* in order for the rewrite rule to be applied.
* If no premise is supplied then it is taken to be a rule that always applies.
*
* This is integrating a datalog approach into the e-graph.
* Technically egglog does something similar but that's not where my inspiration came from.
* The rewrite rules will be like:
*   val ruleName by lim(f+g) to lim(f) + lim(g) if lim(f) = Const and lim(g) = Const
*
*
* From what I can see there are three forms coming through
*
* expression present -> rewrite expression
*
* condition present -> new condition
*
* condition present -> new expression
*
* expression present -> condition present -> rewrite rule
*
* TODO: should the equation be part of the expression type?
*
* Maybe the equation should just be a "where X equal Y"
*
* rewrite {this to that} where {this equal something else}
*
* */

// TODO: this rule breaks the a * b * c equivalence test and somehow puts b and a*b in the same eclass...
val multiplicativeCancellation by rewriteRule { x to y } where { z notEqual 0.c and (z * x equal z * y) }