import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import maths.core.ast.derived
import maths.core.dsl.c
import maths.core.dsl.function
import maths.core.dsl.invoke
import maths.core.dsl.of
import maths.core.dsl.plus
import maths.core.dsl.times
import maths.core.dsl.v
import maths.core.dsl.variable
import maths.core.egraph.AnyNode
import maths.core.egraph.MathsEGraph
import maths.core.egraph.eMatch

class FunctionTests: StringSpec({
    "The identity function can be defined and returns the expression that is passed to it when called" {
        val x by variable()
        val f by function(x) { x }

        f.name shouldBe "f"
        f.params shouldHaveSize 1
        f.params shouldContain x
        f.functionBody shouldBe x

        val y by variable()
        val z by variable()

        f(y) shouldBe y
        f(y + 1) shouldBe y + 1
        f(y * z) shouldBe y * z
        shouldThrowAny {
            f()
        }.message shouldBe "Function call requires ${1} arguments but ${0} were given."
    }

    "A function that uses addition can be defined" {
        val x by variable()
        val myFunc by function(x) { x + 1 }

        myFunc.name shouldBe "myFunc"
        myFunc.params shouldHaveSize 1
        myFunc.params shouldContain x
        myFunc.functionBody shouldBe x + 1
    }

    "A multivariate function can be defined" {
        val x by variable()
        val y by variable()
        val g by function(x, y) { x + y }

        g.name shouldBe "g"
        g.params shouldHaveSize 2
        g.params shouldContain x
        g.params shouldContain y
        g.functionBody shouldBe x + y
    }

    "A function can be invoked" {
        val x by variable()
        val f by function(x) { x + 1 }

        val y by variable()

        val result = f(y)

        result shouldBe y + 1
    }

    "Functions can be composed" {
        val x by variable()
        val f by function(x) { x + 1 }

        f.name shouldBe "f"
        f.params shouldHaveSize 1
        f.params shouldContain x
        f.functionBody shouldBe x + 1

        val y by variable()
        val g by function(y) { 2 * y }

        g.name shouldBe "g"
        g.params shouldHaveSize 1
        g.params shouldContain y
        g.functionBody shouldBe 2 * y

        f of g shouldBe f(g)

        val fOfG = f of g

        fOfG.name shouldBe "f of g"
        fOfG.params shouldHaveSize 1
        fOfG.params shouldContain y
        fOfG.params shouldNotContain x
        fOfG.functionBody shouldBe 2 * y + 1
    }

//    "A function can be represented in an e-graph" {
//        // create e-graph
//        val egg = MathsEGraph()
//
//        // unable to find function in e-graph
//        //egg.eMatch()
//
//        // create function
//        val x by variable()
//        val f by function(x) { x }
//        // add function to e-graph
//        egg.add(f)
//        // able to find function in e-graph
//        // this should include the function parameters
//    }
//
//    "A function cannot have the same parameter defined twice" {
//        shouldThrowAny {
//            val x by variable()
//            val f by function(x, x) { x + 1 }
//        }
//    }

    "Functions can be differentiated" {
        val x by variable()
        val f by function(x) { x + 1 }

        val fPrime = f.derived()

        fPrime shouldBe 1
    }
})
