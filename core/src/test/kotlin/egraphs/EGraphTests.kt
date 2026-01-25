package egraphs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v
import maths.core.verification.EBinary
import maths.core.verification.EClass
import maths.core.verification.EConst
import maths.core.verification.EVar
import maths.core.verification.MathsEGraph
import maths.core.verification.toHashKey

class EGraphTests : StringSpec({
    "An egraph should be empty when created" {
        with(MathsEGraph()) {
            eClasses shouldBe emptyList()
            eNodes shouldBe emptyList()
            eClassesById shouldBe emptyMap()
            eNodeHashCons shouldBe emptyMap()
            latestId shouldBe 0
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
            eClassesById shouldHaveSize 1
            eClassesById shouldBe mutableMapOf(expectedEClass.id to expectedEClass)
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
            eClassesById shouldHaveSize 1
            eClassesById shouldBe mutableMapOf(expectedEClass.id to expectedEClass)
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

            val expectedOperationENode = EBinary(expectedLeftEClass.id,"+", expectedRightEClass.id)
            val expectedOperationEClass = EClass(2, mutableListOf(expectedOperationENode))

            eClasses shouldHaveSize 3
            eClasses shouldBe mutableListOf(expectedLeftEClass, expectedRightEClass, expectedOperationEClass)
            eNodes shouldHaveSize 3
            eNodes shouldBe mutableListOf(expectedLeftENode, expectedRightENode, expectedOperationENode)
            eClassesById shouldHaveSize 3
            eClassesById shouldBe mutableMapOf(
                expectedLeftEClass.id to expectedLeftEClass,
                expectedRightEClass.id to expectedRightEClass,
                expectedOperationEClass.id to expectedOperationEClass,
            )
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