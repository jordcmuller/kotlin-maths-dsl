package maths.core.rewriting

import maths.core.ast.Add
import maths.core.ast.BinaryExpr
import maths.core.ast.Mul
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.ConstMatcher


val multiplicativeCommutativity = RewriteRule(BinaryMatcher(AnyNode, MUL, AnyNode)) {
    if (it !is BinaryExpr || it.operation != MUL) null
    else Mul(it.right, it.left)
}
val multiplicativeAssociativity = RewriteRule(BinaryMatcher(BinaryMatcher(AnyNode, MUL, AnyNode), MUL, AnyNode)) {
    if (it !is BinaryExpr || it.operation != MUL) null
    else if (it.left !is BinaryExpr || it.left.operation != MUL) null
    else Mul(it.left.left, Mul(it.left.right, it.right))
}
val multiplicativeIdentity = RewriteRule(BinaryMatcher(AnyNode, MUL, ConstMatcher(1.0))) {
    if (it !is BinaryExpr || it.operation != MUL) null
    else it.left
}

val additiveCommutativity = RewriteRule(BinaryMatcher(AnyNode, ADD, AnyNode)) {
    if (it !is BinaryExpr || it.operation != ADD) null
    else Add(it.right, it.left)
}
val additiveAssociativity = RewriteRule(BinaryMatcher(BinaryMatcher(AnyNode, ADD, AnyNode), ADD, AnyNode)) {
    if (it !is BinaryExpr || it.operation != ADD) null
    else if (it.left !is BinaryExpr || it.left.operation != ADD) null
    else Add(it.left.left, Add(it.left.right, it.right))
}
val additiveIdentity = RewriteRule(BinaryMatcher(AnyNode, ADD, ConstMatcher(0.0))) {
    if (it !is BinaryExpr || it.operation != ADD) null
    else it.left
}

