package maths.core

import maths.logic.Proof
import kotlin.collections.forEach

fun checkProof(proof: Proof) {
    proof.statements.forEach {
        check(it)
    }
}

fun check(statement: MObject) {
    when (statement) {
        is EqualToExpression -> {
            assert(statement.left == statement.right)
        }
    }
}