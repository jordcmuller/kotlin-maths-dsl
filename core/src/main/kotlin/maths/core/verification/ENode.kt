package maths.core.verification

open class ENode(val identifier: String, var children: List<EClassId> = listOf())

data class EUnary(val operation: String, val operand: EClassId) :
    ENode(operation, listOf(operand))

data class EBinary(val left: EClassId, val operation: String, val right: EClassId) :
    ENode(operation, listOf(left, right))

data class EConst(val value: Double) : ENode(value.toString())
data class EVar(val name: String) : ENode(name)