package egraphs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v
import maths.core.verification.EGraph
import maths.core.verification.MathsLowerer
import maths.core.verification.print

class EGraphDebuggingOutputTests : StringSpec({
    "A constant in the egraph should show debug output" {
        val eGraph = EGraph(MathsLowerer())

        eGraph.add(1.c)

        val debugOutput = eGraph.print()

        val expectedOutput = """
            EGraph
                EClass 0
                    1.0
        """.trimIndent()

        debugOutput shouldBe expectedOutput
    }

    "A variable in the egraph should show debug output" {
        val eGraph = EGraph(MathsLowerer())

        eGraph.add("x".v)

        eGraph.print() shouldBe """
            EGraph
                EClass 0
                    x
        """.trimIndent()
    }

    "Two variables in the egraph should show debug output" {
        val eGraph = EGraph(MathsLowerer())

        eGraph.add("x".v)
        eGraph.add("y".v)

        val debugOutput = eGraph.print()

        val expectedOutput = """
            EGraph
                EClass 0
                    x
                EClass 1
                    y
        """.trimIndent()

        debugOutput shouldBe expectedOutput
    }

    "A composite expression in the egraph should show debug output" {
        val eGraph = EGraph(MathsLowerer())

        eGraph.add("x".v + "y".v)

        val debugOutput = eGraph.print()

        val expectedOutput = """
            EGraph
                EClass 0
                    x
                EClass 1
                    y
                EClass 2
                    +(0 1)
        """.trimIndent()

        debugOutput shouldBe expectedOutput
    }
})