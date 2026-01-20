package egraphs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.verification.EClass
import maths.core.verification.EGraph
import maths.core.verification.ENode
import maths.core.verification.MathsLowerer
import maths.core.verification.UnionFind

class UnionFindTests : StringSpec({
    "An egraph should be empty when created" {
        with(UnionFind()) {
            parents shouldHaveSize 0
        }
    }
    "A constant can be added to the egraph and will reflect in the state" {
        with(UnionFind()) {
            add(0)
            parents shouldHaveSize 1
            parents[0] shouldBe 0
            find(0) shouldBe 0

            add(1)
            parents shouldHaveSize 2
            parents[1] shouldBe 1
            find(1) shouldBe 1

            union(0, 1)
            parents shouldHaveSize 2
            find(0) shouldBe 0
            find(1) shouldBe 0

            add(2)
            parents shouldHaveSize 3
            find(2) shouldBe 2

            union(2, 0)
            parents shouldHaveSize 3
            find(0) shouldBe 2
            find(1) shouldBe 2
            find(2) shouldBe 2
        }
    }
})