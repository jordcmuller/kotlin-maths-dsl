package maths.patterns

object RuleRegistry {
    private val registry: MutableMap<AlgebraicRule, RuleMatcher> = mutableMapOf()

    init {
        // Register built-ins
        register(Commutative, CommutativeMatcher)
        register(Associative, AssociativeMatcher)
    }

    fun register(rule: AlgebraicRule, matcher: RuleMatcher) {
        registry[rule] = matcher
    }

    fun matcherFor(rule: AlgebraicRule): RuleMatcher? = registry[rule]
}
