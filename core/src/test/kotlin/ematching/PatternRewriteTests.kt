package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.ast.BinaryExpr
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.egraph.querying.PatternCondition
import maths.core.rewriting.dsl.provideDelegate
import maths.core.egraph.AnyNode
import maths.core.egraph.BinaryMatcher
import maths.core.egraph.EClass
import maths.core.egraph.EMatchResult
import maths.core.egraph.EVar
import maths.core.egraph.extraction.canonicalForm

class PatternRewriteTests : StringSpec({
    "A rewrite rule should be simple and map a pattern to a template" {
        val x by variable()
        val y by variable()
        val additiveCommutativity by x + y to y + x

        additiveCommutativity.name shouldBe "additiveCommutativity"
        (additiveCommutativity.query.premises.first() as PatternCondition).pattern shouldBe BinaryMatcher(AnyNode("x"), ADD, AnyNode("y"))
        additiveCommutativity.template shouldBe BinaryMatcher(AnyNode("y"), ADD, AnyNode("x"))

        val xEClass = EClass(1).apply { nodes.add(EVar(x.name))}
        val yEClass = EClass(2).apply { nodes.add(EVar(y.name))}

        val matchResult = EMatchResult(mapOf("x" to xEClass.canonicalForm, "y" to yEClass.canonicalForm))
        val rewritten = additiveCommutativity.rewrite(matchResult)

        rewritten shouldBe BinaryExpr(yEClass.canonicalForm, ADD, xEClass.canonicalForm)
    }
})
