package egraphs

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.v
import maths.core.verification.MathsEGraph
import maths.core.verification.toMermaid

class EGraphMermaidGraphTests : StringSpec({
    "A constant in the egraph should generate a mermaid diagram" {
        val eGraph = MathsEGraph()

        eGraph.add(1.c)

        val debugOutput = eGraph.toMermaid()

        val expectedOutput = """
            graph TD

                subgraph EClass0["0"]
                    style EClass0 stroke-dasharray: 5 5
                    E0_N0["1.0"]
                end
        """.trimIndent()

        debugOutput shouldBe expectedOutput
    }

    "A variable in the egraph should generate a mermaid diagram" {
        val eGraph = MathsEGraph()

        eGraph.add("x".v)

        val debugOutput = eGraph.toMermaid()

        val expectedOutput = """
            graph TD

                subgraph EClass0["0"]
                    style EClass0 stroke-dasharray: 5 5
                    E0_N0["x"]
                end
        """.trimIndent()

        debugOutput shouldBe expectedOutput
    }

    "Two variables in the egraph should generate a mermaid diagram" {
        val eGraph = MathsEGraph()

        eGraph.add("x".v)
        eGraph.add("y".v)

        val debugOutput = eGraph.toMermaid()

        val expectedOutput = """
            graph TD

                subgraph EClass0["0"]
                    style EClass0 stroke-dasharray: 5 5
                    E0_N0["x"]
                end

                subgraph EClass1["1"]
                    style EClass1 stroke-dasharray: 5 5
                    E1_N0["y"]
                end
        """.trimIndent()

        debugOutput shouldBe expectedOutput
    }

    "A composite expression in the egraph should generate a mermaid diagram" {
        val eGraph = MathsEGraph()

        eGraph.add("x".v + "y".v)

        eGraph.toMermaid() shouldBe """
            graph TD

                subgraph EClass0["0"]
                    style EClass0 stroke-dasharray: 5 5
                    E0_N0["x"]
                end

                subgraph EClass1["1"]
                    style EClass1 stroke-dasharray: 5 5
                    E1_N0["y"]
                end

                subgraph EClass2["2"]
                    style EClass2 stroke-dasharray: 5 5
                    E2_N0["+"]
                end

                E2_N0 --> EClass0
                E2_N0 --> EClass1
        """.trimIndent()
    }

    "A composite expression in the egraph should generate a mermaid diagram which updates when the operands are merged" {
        val eGraph = MathsEGraph()

        eGraph.add("x".v + "y".v)

        eGraph.toMermaid() shouldBe """
            graph TD

                subgraph EClass0["0"]
                    style EClass0 stroke-dasharray: 5 5
                    E0_N0["x"]
                end

                subgraph EClass1["1"]
                    style EClass1 stroke-dasharray: 5 5
                    E1_N0["y"]
                end

                subgraph EClass2["2"]
                    style EClass2 stroke-dasharray: 5 5
                    E2_N0["+"]
                end

                E2_N0 --> EClass0
                E2_N0 --> EClass1
        """.trimIndent()

        eGraph.mergeAndRebuild(0, 1)

        eGraph.toMermaid() shouldBe """
            graph TD

                subgraph EClass0["0"]
                    style EClass0 stroke-dasharray: 5 5
                    E0_N0["x"]
                    E0_N1["y"]
                end

                subgraph EClass2["2"]
                    style EClass2 stroke-dasharray: 5 5
                    E2_N0["+"]
                end

                E2_N0 --> EClass0
                E2_N0 --> EClass0
        """.trimIndent()
    }
})