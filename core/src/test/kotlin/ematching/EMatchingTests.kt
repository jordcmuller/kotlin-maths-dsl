package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v
import maths.core.egraph.AnyNode
import maths.core.egraph.MathsEGraph
import maths.core.egraph.eMatch
import maths.core.egraph.extraction.canonicalForm

class EMatchingTests : StringSpec({
    val egg = MathsEGraph()

    val xEClass = egg.add("x".v)
    val yEClass = egg.add("y".v)
    val oneEClass = egg.add(1.c)
    val twoEClass = egg.add(2.c)
    val xPlusYEClass = egg.add("x".v + "y".v)
    val onePlusTwoEClass = egg.add(1.c + 2.c)
    val xPlusTwoEClass = egg.add("x".v + 2.c)
    val xPlusYPlusOneEClass = egg.add("x".v + "y".v + 1.c)

    "AnyNode should match all nodes" {
        val result = egg.eMatch(AnyNode("any"))

        result.size shouldBe 8
        result[0].matchedExpressions["any"] shouldBe xEClass.canonicalForm
        result[1].matchedExpressions["any"] shouldBe yEClass.canonicalForm
        result[2].matchedExpressions["any"] shouldBe oneEClass.canonicalForm
        result[3].matchedExpressions["any"] shouldBe twoEClass.canonicalForm
        result[4].matchedExpressions["any"] shouldBe xPlusYEClass.canonicalForm
        result[5].matchedExpressions["any"] shouldBe onePlusTwoEClass.canonicalForm
        result[6].matchedExpressions["any"] shouldBe xPlusTwoEClass.canonicalForm
        result[7].matchedExpressions["any"] shouldBe xPlusYPlusOneEClass.canonicalForm
    }
//    "AnyNode should not cause an infinite loop" {
//         // set up an identity rule like x+0=x
//         // try match with AnyNode
//    }
//    "AnyConst should match all constants" {
//        val result = egg.eMatch(AnyConst)
//
//        result.size shouldBe 2
//        result[0].matchId shouldBe 3
//        result[0].matchedGroups[] shouldBe EMLeaf(3)
//        result[1].match shouldBe EMLeaf(4)
//    }
//    "AnyVar should match all variables" {
//        eMatch(egg, AnyVar).size shouldBe 2
//
//        val result = eMatch(egg, AnyVar)
//
//        result.size shouldBe 2
//        result[0].match shouldBe EMLeaf(1)
//        result[1].match shouldBe EMLeaf(2)
//    }
//    "ConstMatcher" {
//        val oneResult = eMatch(egg, ConstMatcher(1.0))
//
//        oneResult.size shouldBe 1
//        oneResult[0].match shouldBe EMLeaf(3)
//
//        val twoResult = eMatch(egg, ConstMatcher(2.0))
//
//        twoResult.size shouldBe 1
//        twoResult[0].match shouldBe EMLeaf(4)
//    }
//    "VarMatcher" {
//        val xResult = eMatch(egg, VarMatcher("x"))
//
//        xResult.size shouldBe 1
//        xResult[0].match shouldBe EMLeaf(3)
//
//        val yResult = eMatch(egg, VarMatcher("y"))
//
//        yResult.size shouldBe 1
//        yResult[0].match shouldBe EMLeaf(4)
//    }
//    "BinaryMatcher" {
//        val anyBinaryResult = eMatch(egg, BinaryMatcher(AnyNode("a"), listOf(ADD), AnyNode("b")))
//
//        anyBinaryResult.size shouldBe 4
//        anyBinaryResult[0].match shouldBe EMBinary(5, EMNamedLeaf(1, "a"), ADD, EMNamedLeaf(2, "b"))
//        anyBinaryResult[1].match shouldBe EMBinary(6, EMNamedLeaf(3, "a"), ADD, EMNamedLeaf(4, "b"))
//        anyBinaryResult[2].match shouldBe EMBinary(7, EMNamedLeaf(1, "a"), ADD, EMNamedLeaf(4, "b"))
//        anyBinaryResult[3].match shouldBe EMBinary(8, EMNamedLeaf(5, "a"), ADD, EMNamedLeaf(3, "b"))
//    }
})