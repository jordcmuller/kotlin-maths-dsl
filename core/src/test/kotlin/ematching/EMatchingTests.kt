package ematching

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import maths.core.dsl.c
import maths.core.verification.AnyConst
import maths.core.verification.EMLeaf
import maths.core.verification.MathsEGraph
import maths.core.verification.eMatch

class EMatchingTests : StringSpec({
    "AnyNode should match all nodes" {


    }
    "AnyNode should not cause an infinite loop" {}
    "AnyConst should match all constants" {
        val egg = MathsEGraph()

        egg.add(1.c)

        val result = eMatch(egg, AnyConst)

        result shouldContain EMLeaf(1)
    }
    "AnyVar should match all variables" {}
    "ConstMatcher" {}
    "VarMatcher" {}
    "BinaryMatcher" {}
})