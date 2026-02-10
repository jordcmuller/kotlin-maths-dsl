package maths.core.rewriting

import maths.core.rewriting.dsl.anyNode
import maths.core.rewriting.dsl.provideDelegate
import maths.core.verification.ConstMatcher
import maths.core.verification.div
import maths.core.verification.minus
import maths.core.verification.plus
import maths.core.verification.times
import maths.core.verification.unaryMinus

val x by anyNode()
val y by anyNode()
val z by anyNode()

val multiplicativeCommutativity by x * y to y * x
val multiplicativeAssociativity by x * y * z to x * (y * z)
val multiplicativeIdentity by x * ConstMatcher(1.0) to x
val multiplicativeInverse by x / y to x * (1 / y)

val additiveCommutativity by x + y to y + x
val additiveAssociativity by x + y + z to x + (y + z)
val additiveIdentity by x + ConstMatcher(0.0) to x
val additiveInverse by x - y to x + (-y)

val distributivity by x * (y + z) to x * y + x * z
