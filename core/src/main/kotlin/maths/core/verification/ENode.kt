package maths.core.verification

open class ENode(val identifier: String, var childEClasses: List<EClass> = listOf()) {
    lateinit var parentEClass: EClass
}

data class EUnary(val operation: String, val operand: EClass) :
    ENode(operation, listOf(operand))

data class EBinary(val left: EClass, val operation: String, val right: EClass) :
    ENode(operation, listOf(left, right))

data class EConst(val value: Double) : ENode(value.toString())
data class EVar(val name: String) : ENode(name)