//import maths.core.compare.expressionsEquivalent
//import maths.core.algebra.AlgebraRegistry
//import maths.core.algebra.installDefaultRules
//import maths.core.algebra.simplify
//import maths.core.dsl.eq
//import maths.core.dsl.plus
//import maths.core.dsl.squared
//import maths.core.dsl.times
//import maths.core.dsl.variable
//import maths.core.format.latex
//import maths.core.format.raw
//import maths.core.format.readable
//import maths.core.solve.solveQuadratic

// ---------------------------
// Example usage
// ---------------------------
//fun mainExample() {
//    installDefaultRules()
//
//    // Variables (auto-named from property names)
//    val x by variable()
//
//    // Equation: x² + 2x + 1 = 0
//    val eq = x.squared + 2 + 3 * x + 1 eq 0
//
//    println("Equation: ${eq.raw()}")
//
//    // Simplify the left-hand side
//    val (simplifiedLeft, derivation) = simplify(eq.left, AlgebraRegistry.listRules())
//    println("Simplified left: ${simplifiedLeft.raw()}")
//    derivation.printTrace()
//
//    // Check equivalence to (x + 1)²
//    val targetForm = (x + 1).squared
//    println("Equivalent to (x + 1)² ? ${expressionsEquivalent(simplifiedLeft, targetForm)}")
//
//    // Solve
//    val solutions = solveQuadratic(eq)
//    println("Solutions: $solutions")
//
//    println("Raw:       ${eq.raw()}")
//    println("Readable:  ${eq.readable()}")
//    println("LaTeX:     ${eq.latex()}")
//}

fun main() {
//    mainExample()
}
