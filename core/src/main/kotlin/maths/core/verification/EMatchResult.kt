package maths.core.verification

class EMatchResult(val rootEClass: EClass, val matchedGroups: Map<String, EClass> = emptyMap()) {
    operator fun get(anyNode: AnyNode) = matchedGroups[anyNode.name] ?: error("matched group ${anyNode.name} not found")
}
