package maths

import maths.core.dsl.*

fun `Check that constants are equal`() {
    maths { 1.c eq 1.c }
}

fun `Check that variables are equal`() {
    maths {
        val x by variable()
        x eq x   
    }
}

fun `Check that different variables are not immediately equal`() {
    maths {
        val x by variable()
        val y by variable()
        x eq y
    }
}

fun `Check that the same numeric expression is equal`() {
    maths {
        1.c + 2.c eq 1.c + 2.c
    }
}

fun AnotherTest() {
    maths {
        val x by variable()
        2.c + 3.c eq 5.c // equivalent
        x eq 5.c // equivalent
        x eq 6.c // equivalent
//        2.c eq 3.c // ❌ not equivalent
    }
}

fun main() {
    AnotherTest()
//    `Check that constants are equal`()
//    `Check that variables are equal`()
//    `Check that different variables are not immediately equal`()
//    `Check that the same numeric expression is equal`()
}