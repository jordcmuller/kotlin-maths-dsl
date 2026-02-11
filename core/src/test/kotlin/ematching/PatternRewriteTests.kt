package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.rewriting.dsl.provideDelegate
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.EClass
import maths.core.verification.EMatchResult
import maths.core.verification.RRBinary
import maths.core.verification.RRLeaf

class PatternRewriteTests : StringSpec({
    "A rewrite rule should be simple and map a pattern to a template" {
        val x by variable()
        val y by variable()
        val additiveCommutativity by x + y to y + x

        additiveCommutativity.name shouldBe "additiveCommutativity"
        additiveCommutativity.pattern shouldBe BinaryMatcher(AnyNode("x"), ADD, AnyNode("y"))
        additiveCommutativity.template shouldBe BinaryMatcher(AnyNode("y"), ADD, AnyNode("x"))

        val xEClass = EClass(1)
        val yEClass = EClass(2)
        val xPlusYEClass = EClass(3)

        val matchResult = EMatchResult(xPlusYEClass, mapOf("x" to xEClass, "y" to yEClass))
        val rewritten = additiveCommutativity.rewrite(matchResult)

        rewritten shouldBe RRBinary(RRLeaf(yEClass), ADD, RRLeaf(xEClass))
    }
})
