package maths.core.rewriting

import maths.core.dsl.div
import maths.core.dsl.minus
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.dsl.unaryMinus
import maths.core.dsl.variable
import maths.core.rewriting.dsl.provideDelegate

val x by variable()
val y by variable()
val z by variable()

val multiplicativeCommutativity by x * y to y * x
val multiplicativeAssociativity by x * y * z to x * (y * z)
val multiplicativeIdentity by x * 1 to x
val multiplicativeInverse by x / y to x * (1 / y)

val additiveCommutativity by x + y to y + x
val additiveAssociativity by x + y + z to x + (y + z)
val additiveIdentity by x + 0 to x
val additiveInverse by x - y to x + (-y)

val distributivity by x * (y + z) to x * y + x * z
