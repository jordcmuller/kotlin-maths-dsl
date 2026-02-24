package egraphs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v
import maths.core.egraph.EBinary
import maths.core.egraph.EClass
import maths.core.egraph.EConst
import maths.core.egraph.EVar
import maths.core.egraph.MathsEGraph
import maths.core.egraph.toHashKey

class EGraphTests : StringSpec({
    "An egraph should be empty when created" {
        with(MathsEGraph()) {
            eClasses shouldBe emptyList()
            eNodes shouldBe emptyList()
            eNodeHashCons shouldBe emptyMap()
            latestId shouldBe 1
            worklist shouldBe emptyList()
        }
    }
    "An EConst term can be added to the egraph and will reflect in the state" {
        with(MathsEGraph()) {
            add(1.c)

            val expectedENode = EConst(1.0)
            val expectedEClass = EClass(0, mutableListOf(expectedENode))

            eClasses shouldBe mutableListOf(expectedEClass)
            eNodes shouldBe mutableListOf(expectedENode)
            eNodeHashCons shouldHaveSize 1
            eNodeHashCons shouldBe mutableMapOf(expectedENode.toHashKey to expectedEClass.id)
            latestId shouldBe 1
            worklist shouldBe emptyList()
        }
    }
    "A variable can be added to the egraph and will reflect in the state" {
        with(MathsEGraph()) {
            add("x".v)

            val expectedENode = EVar("x")
            val expectedEClass = EClass(0, mutableListOf(expectedENode))

            eClasses shouldBe mutableListOf(expectedEClass)
            eNodes shouldBe mutableListOf(expectedENode)
            eNodeHashCons shouldHaveSize 1
            eNodeHashCons shouldBe mutableMapOf(expectedENode.toHashKey to expectedEClass.id)
            latestId shouldBe 1
            worklist shouldBe emptyList()
        }
    }
    "A composite expression can be added to the egraph and will reflect in the state" {
        with(MathsEGraph()) {
            add("x".v + 1.c)

            val expectedLeftENode = EVar("x")
            val expectedLeftEClass = EClass(0, mutableListOf(expectedLeftENode))

            val expectedRightENode = EConst(1.0)
            val expectedRightEClass = EClass(1, mutableListOf(expectedRightENode))

            val expectedOperationENode = EBinary(expectedLeftEClass,"+", expectedRightEClass)
            val expectedOperationEClass = EClass(2, mutableListOf(expectedOperationENode))

            eClasses shouldHaveSize 3
            eClasses shouldBe mutableListOf(expectedLeftEClass, expectedRightEClass, expectedOperationEClass)
            eNodes shouldHaveSize 3
            eNodes shouldBe mutableListOf(expectedLeftENode, expectedRightENode, expectedOperationENode)
            eNodeHashCons shouldHaveSize 3
            eNodeHashCons shouldBe mutableMapOf(
                expectedLeftENode.toHashKey to expectedLeftEClass.id,
                expectedRightENode.toHashKey to expectedRightEClass.id,
                expectedOperationENode.toHashKey to expectedOperationEClass.id,
            )
            latestId shouldBe 3
            worklist shouldBe emptyList()
        }
    }
    "Two eclasses can be merged which won't change congruence closure but the state but will add to the worklist" {}
    "When two eclasses are merged and the egraph is rebuilt then the congruence closure will be true" {}
})