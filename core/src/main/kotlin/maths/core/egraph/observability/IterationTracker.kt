package maths.core.egraph.observability

import maths.core.egraph.EClass
import maths.core.egraph.ENode
import maths.core.rewriting.RewriteRule

class IterationTracker: BaseObserver() {
    val snapshots = mutableListOf<IterationSnapshot>()

    override fun onIterationStart() {
        println("IterationTracker.onIterationStart")
    }

    override fun onIterationEnd() {
        println("IterationTracker.onIterationEnd")
        snapshots.add(IterationSnapshot(0))
    }
}

data class IterationSnapshot(val iterationNumber: Int) {
    lateinit var newENodes: List<ENode>
    lateinit var newEClasses: List<EClass>
    lateinit var merges: List<List<EClass>>
    lateinit var ruleAnalytics: Map<RewriteRule, RuleStats>
}

data class RuleStats(val resultCount: Int, val effectiveAdditions: Int)


class SaturationSummary {
    var totalIterations: Int = 0
    var totalENodes: Int = 0
    var totalEClasses: Int = 0
    var totalMerges: Int = 0
    var top5RulesByFireCount: Int = 0
    var largestEClassSize: Int = 0
}
