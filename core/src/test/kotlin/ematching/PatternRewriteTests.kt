package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.BinaryExpr
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.rewriting.dsl.provideDelegate
import maths.core.verification.AnyNode
import maths.core.verification.BinaryMatcher
import maths.core.verification.EBinary
import maths.core.verification.EClass
import maths.core.verification.EMatchResult
import maths.core.verification.EVar
import maths.core.verification.extraction.canonicalForm

class PatternRewriteTests : StringSpec({
    "A rewrite rule should be simple and map a pattern to a template" {
        val x by variable()
        val y by variable()
        val additiveCommutativity by x + y to y + x

        additiveCommutativity.name shouldBe "additiveCommutativity"
        additiveCommutativity.pattern shouldBe BinaryMatcher(AnyNode("x"), ADD, AnyNode("y"))
        additiveCommutativity.template shouldBe BinaryMatcher(AnyNode("y"), ADD, AnyNode("x"))

        val xEClass = EClass(1).apply { nodes.add(EVar(x.name))}
        val yEClass = EClass(2).apply { nodes.add(EVar(y.name))}
        val xPlusYEClass = EClass(3).apply { nodes.add(EBinary(xEClass, "+", yEClass))}

        val matchResult = EMatchResult(xPlusYEClass, mapOf("x" to xEClass.canonicalForm, "y" to yEClass.canonicalForm))
        val rewritten = additiveCommutativity.rewrite(matchResult)

        rewritten shouldBe BinaryExpr(yEClass.canonicalForm, ADD, xEClass.canonicalForm)
    }
})
