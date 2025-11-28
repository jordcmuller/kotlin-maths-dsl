package maths.logic

import maths.core.AdditionExpression
import maths.core.EqualToExpression
import maths.core.ImpliesExpression
import maths.core.IntExpression
import maths.core.MObject
import maths.core.checkProof

class Proof(val statements: MutableList<MObject> = mutableListOf()) {

    fun prove() {
        // Go through statements and check them. Kinda similar to an interpreter?
        checkProof(this)
    }

    infix fun MObject.equals(mObject: MObject): MObject {
        statements.add(EqualToExpression(this, mObject))
        return mObject
    }

    infix fun MObject.implies(mObject: MObject): MObject {
        statements.add(ImpliesExpression(this, mObject))
        return mObject
    }

    operator fun MObject.plus(mObject: MObject): MObject {
        return AdditionExpression(this, mObject)
    }

    val Int.asMO: IntExpression get() = IntExpression(this)
    
}

fun proof(block: Proof.() -> Unit) {
    return Proof().apply(block).prove().apply { println("Proof successful") }
}
