package maths.core.egraph

import maths.core.egraph.analysis.ConstValue

sealed class ENode(val identifier: String, var childEClasses: List<EClass> = listOf()) {
    lateinit var parentEClass: EClass
}

data class EUnary(val operation: String, val operand: EClass) :
    ENode(operation, listOf(operand)) {

    override fun toString(): String {
        return "$operation($operand)"
    }
}

data class EBinary(val left: EClass, val operation: String, val right: EClass) :
    ENode(operation, listOf(left, right)) {

    override fun toString(): String {
        return "($left) $operation ($right)"
    }
}

data class EConst(val value: ConstValue) : ENode(value.toString()) {
    override fun toString(): String {
        return "$value"
    }
}
data class EVar(val name: String) : ENode(name) {
    override fun toString(): String {
        return name
    }
}