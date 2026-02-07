package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v
import maths.core.verification.AnyNode
import maths.core.verification.MathsEGraph
import maths.core.verification.eMatch

class EMatchingTests : StringSpec({
    val egg = MathsEGraph()

    egg.add("x".v)
    egg.add("y".v)
    egg.add(1.c)
    egg.add(2.c)
    egg.add("x".v + "y".v)
    egg.add(1.c + 2.c)
    egg.add("x".v + 2.c)
    egg.add("x".v + "y".v + 1.c)

    "AnyNode should match all nodes" {
        val result = egg.eMatch(AnyNode("any"))

        result.size shouldBe 8
        result[0].matchedGroups["any"] shouldBe 1
        result[1].matchedGroups["any"] shouldBe 2
        result[2].matchedGroups["any"] shouldBe 3
        result[3].matchedGroups["any"] shouldBe 4
        result[4].matchedGroups["any"] shouldBe 5
        result[5].matchedGroups["any"] shouldBe 6
        result[6].matchedGroups["any"] shouldBe 7
        result[7].matchedGroups["any"] shouldBe 8
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