package maths.core.egraph

interface ENode {
    val identifier: String
    var childEClasses: List<EClass>
    var parentEClass: EClass
}

open class BaseENode(override val identifier: String, override var childEClasses: List<EClass> = listOf()): ENode {
    override lateinit var parentEClass: EClass
}

data class EUnary(val operation: String, val operand: EClass) :
    BaseENode(operation, listOf(operand)) {

    override fun toString(): String {
        return "$operation($operand)"
    }
}

data class EBinary(val left: EClass, val operation: String, val right: EClass) :
    BaseENode(operation, listOf(left, right)) {

    override fun toString(): String {
        return "($left) $operation ($right)"
    }
}

open class EConst(val value: Any) : BaseENode(value.toString()) {
    override fun toString(): String {
        return "$value"
    }
}
data class EVar(val name: String) : BaseENode(name) {
    override fun toString(): String {
        return name
    }
}