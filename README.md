# kotlin-maths-dsl

Kotlin has a set of language features that work together to create domain specific languages, generally referred to as DSLs.
There are a few epic examples of this, and they showcase the power of kotlin as an intuitive and expressive programming language.

The `kotlin-maths-dsl` is designed to provide symbolic and structural representations of equations, allowing the user to
work with complex mathematical objects in an intuitive and manageable way.

<!-- TOC -->
* [kotlin-maths-dsl](#kotlin-maths-dsl)
  * [A Simple Example](#a-simple-example)
  * [E-Graphs](#e-graphs)
    * [Rewrite Rules](#rewrite-rules)
    * [Equality Saturation](#equality-saturation)
  * [References](#references)
<!-- TOC -->

## A Simple Example

Have a look at the example from [examples/example1.kt](core/src/main/kotlin/examples/example1.kt)

This shows how you can create a maths context, declare some variables, and then reason about them mathematically.
Behind the scenes, there is an equivalence graph, generally referred to as an e-graph, working some magic. See the [EGraphs](#e-graphs) section for more info on these! 

```kotlin
maths {
    val x by variable()
    val y by variable()

    if (x notEqual y) println("These are different variables, of course they're not equal")

    val expr1 = y + 1
    val expr2 = x + 1

    if (expr1 notEqual expr2) println("These expressions are also not equal...")

    x equate y

    if (expr1 equal expr2) println("But now they are!")
}
```

## E-Graphs

An e-graph is an awesome data structure for encoding equivalence between many different expressions.

The implementation behind this library is loosely based off a project called egg, an e-graph library built in rust,
which you can read more about here [https://egraphs-good.github.io/](https://egraphs-good.github.io/)

In loose terms, it consists of a set of equivalence classes, each of which contain a set of e-nodes which, as you might expect, are equivalent.
The e-graph doesn't compute the equivalences, but it stores them really well.
It also has an interesting property called congruence closure which ensures that equivalence propagates upwards from operands to functions.

```
f(a) = f(b) if a = b
```

### Rewrite Rules
You can define a set of properties, known as rewrite rules, that allow the e-graph to find many equivalent forms of the stored expressions. 
The rewrite rule has a pattern and a template. The pattern is used to e-match and find all matching expressions in the e-graph.
These matched expressions are then used to generate another expression based on the template in the rewrite rule.

The `kotlin-maths-dsl` library provides a flexible and intuitive way to define properties as rewrite rules.

```kotlin
val x by variable()
val y by variable()
val z by variable()

val commutativity by x + y to y + x
val associativity by x + y + z to x + (y + z)
val distributivity by x * (y + z) to x * y + x * z
val identity by x + 0 to x
```

### Equality Saturation
The rewrite rules are reapplied over and over again until no new changes are seen in the e-graph. This is called equality saturation. 

## References
- [egg](https://egraphs-good.github.io/)
- [e-graph Wikipedia](https://en.wikipedia.org/wiki/E-graph)