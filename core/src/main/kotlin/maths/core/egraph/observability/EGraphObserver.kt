package maths.core.egraph.observability;

import maths.core.egraph.EClass
import maths.core.egraph.analysis.AnalysisData

interface EGraphObserver {
    fun onRuleMatch()
    fun onMerge()
    fun onMergeConflict(firstEClass: EClass, secondEClass: EClass, conflictingAnalysisData: AnalysisData)
    fun onIterationStart()
    fun onIterationEnd()
    fun onExtraction()
}

open class BaseObserver : EGraphObserver {
    override fun onRuleMatch() {
        println("BaseObserver.onRuleMatch")
    }

    override fun onMerge() {
        println("BaseObserver.onMerge")
    }

    override fun onMergeConflict(firstEClass: EClass, secondEClass: EClass, conflictingAnalysisData: AnalysisData) {
        println("BaseObserver.onMergeConflict")
    }

    override fun onIterationStart() {
        println("BaseObserver.onIterationStart")
    }

    override fun onIterationEnd() {
        println("BaseObserver.onIterationEnd")
    }

    override fun onExtraction() {
        println("BaseObserver.onExtraction")
    }
}

