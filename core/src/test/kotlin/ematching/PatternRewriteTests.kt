package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.rewriting.dsl.anyNode
import maths.core.rewriting.dsl.provideDelegate
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.EMatchResult
import maths.core.verification.RRBinary
import maths.core.verification.RRLeaf
import maths.core.verification.plus

class PatternRewriteTests : StringSpec({
    "A rewrite rule should be simple and map a pattern to a template" {
        val x by anyNode()
        val y by anyNode()
        val additiveCommutativity by x + y to y + x

        additiveCommutativity.name shouldBe "additiveCommutativity"
        additiveCommutativity.pattern shouldBe BinaryMatcher(AnyNode("x"), listOf(ADD), AnyNode("y"))
        additiveCommutativity.template shouldBe BinaryMatcher(AnyNode("y"), listOf(ADD), AnyNode("x"))

        val matchResult = EMatchResult(0, mapOf("x" to 1, "y" to 2))
        val rewritten = additiveCommutativity.rewrite(matchResult)

        rewritten shouldBe RRBinary(RRLeaf(2), ADD, RRLeaf(1))
    }
})
