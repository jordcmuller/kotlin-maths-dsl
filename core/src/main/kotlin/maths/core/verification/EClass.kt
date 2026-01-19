package maths.core.verification

typealias EClassId = Int

data class EClass(
    val id: EClassId,
    val nodes: MutableList<ENode> = mutableListOf(),
)