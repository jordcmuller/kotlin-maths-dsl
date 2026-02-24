package egraphs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import maths.core.egraph.EClass
import maths.core.egraph.UnionFind

class UnionFindTests : StringSpec({
    "The UnionFind should be empty when created" {
        with(UnionFind<Int>()) {
            parents shouldHaveSize 0
        }
    }
    "A UnionFind with type Int can have items added, found, and united" {
        with(UnionFind<Int>()) {
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
    "A UnionFind with type EClass can have items added, found, and united" {
        with(UnionFind<EClass>()) {
            val first = EClass(1)

            add(first)
            parents shouldHaveSize 1
            parents[first] shouldBe first
            find(first) shouldBe first

            val second = EClass(2)

            add(second)
            parents shouldHaveSize 2
            parents[second] shouldBe second
            find(second) shouldBe second

            union(first, second)
            parents shouldHaveSize 2
            find(first) shouldBe first
            find(second) shouldBe first

            val third = EClass(3)

            add(third)
            parents shouldHaveSize 3
            parents[third] shouldBe third
            find(third) shouldBe third

            union(third, first)
            parents shouldHaveSize 3
            find(first) shouldBe third
            find(second) shouldBe third
            find(third) shouldBe third
        }
    }
})