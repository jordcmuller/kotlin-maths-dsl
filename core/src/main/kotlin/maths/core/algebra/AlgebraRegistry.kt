package maths.core.algebra
// ---------------------------
// Registry for rules / algebras
// ---------------------------
object AlgebraRegistry {
    private val ruleBuckets = mutableListOf<Rule>()
    fun registerRule(rule: Rule) {
        ruleBuckets.add(rule)
    }

    fun listRules(): List<Rule> = ruleBuckets.toList()
}

