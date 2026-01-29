package maths.core.rewriting

import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.ConstMatcher
import maths.core.verification.EMBinary
import maths.core.verification.RRBinary
import maths.core.verification.RRLeaf
import maths.core.verification.plus
import maths.core.verification.times


val multiplicativeCommutativity = RewriteRule(AnyNode * AnyNode) {
    if (it !is EMBinary || it.operation != MUL) null
    else RRBinary(RRLeaf(it.right.eClassId), MUL, RRLeaf(it.left.eClassId))
}
val multiplicativeAssociativity = RewriteRule(AnyNode * AnyNode * AnyNode) {
    if (it !is EMBinary || it.operation != MUL) null
    else if (it.left !is EMBinary || it.left.operation != MUL) null
    else RRBinary(
        RRLeaf(it.left.left.eClassId),
        MUL,
        RRBinary(
            RRLeaf(it.left.right.eClassId),
            MUL,
            RRLeaf(it.right.eClassId)
        )
    )
}
val multiplicativeIdentity = RewriteRule(AnyNode * ConstMatcher(1.0)) {
    if (it !is EMBinary || it.operation != MUL) null
    else RRLeaf(it.left.eClassId)
}

val additiveCommutativity = RewriteRule(BinaryMatcher(AnyNode, ADD, AnyNode)) {
    if (it !is EMBinary || it.operation != ADD) null
    else RRBinary(RRLeaf(it.right.eClassId), ADD, RRLeaf(it.left.eClassId))
}
val additiveAssociativity = RewriteRule(BinaryMatcher(BinaryMatcher(AnyNode, ADD, AnyNode), ADD, AnyNode)) {
    if (it !is EMBinary || it.operation != ADD) null
    else if (it.left !is EMBinary || it.left.operation != ADD) null
    else RRBinary(
        RRLeaf(it.left.left.eClassId),
        ADD,
        RRBinary(
            RRLeaf(it.left.right.eClassId),
            ADD,
            RRLeaf(it.right.eClassId)
        )
    )
}
val additiveIdentity = RewriteRule(BinaryMatcher(AnyNode, ADD, ConstMatcher(0.0))) {
    if (it !is EMBinary || it.operation != ADD) null
    else RRLeaf(it.left.eClassId)
}


val distributivity = RewriteRule(AnyNode * (AnyNode + AnyNode)) {
    if (it !is EMBinary || it.operation != MUL) null
    else if (it.right !is EMBinary || it.right.operation != ADD) null
    else RRBinary(
        RRBinary(
            RRLeaf(it.left.eClassId),
            MUL,
            RRLeaf(it.right.left.eClassId)
        ),
        ADD,
        RRBinary(
            RRLeaf(it.left.eClassId),
            MUL,
            RRLeaf(it.right.right.eClassId)
        )
    )
}
