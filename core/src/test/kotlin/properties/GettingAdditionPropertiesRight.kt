package properties

import io.kotest.core.spec.style.StringSpec
import maths.core.dsl.MathsContext
import maths.core.dsl.c
import maths.core.dsl.plus
import maths.core.dsl.variable
import maths.core.rewriting.dsl.implies
import maths.core.rewriting.dsl.provideDelegate

class GettingAdditionPropertiesRight: StringSpec({
    val x by variable()
    val y by variable()
    val z by variable()

    val additiveCommutativity by x + y to y + x
    val additiveAssociativity by x + y + z to x + (y + z)
    val additiveIdentity by x + 0 to x

    "Commutativity" {
        MathsContext.empty {
            x + y strictNotEqual y + x

            withRule { additiveCommutativity }

            x + y strictEqual y + x
        }
    }

    "Associativity" {
        MathsContext.empty {
            x + y + z strictNotEqual x + (y + z)
            x + (y + z) strictNotEqual x + y + z

            withRule { additiveAssociativity }

            x + (y + z) strictEqual x + y + z
            x + y + z strictEqual x + (y + z)
        }
    }

    "Identity" {
        MathsContext.empty {
            x + 0 strictNotEqual x
            0 + x strictNotEqual x

            withRule { additiveIdentity }

            x + 0 strictEqual x
            0 + x strictNotEqual x // missing commutativity and identiy rule is right sided
        }
    }

    "Identity with commutativity" {
        MathsContext.empty {
            x + 0 strictNotEqual x
            0 + x strictNotEqual x

            withRule { additiveIdentity }
            withRule { additiveCommutativity }

            x + 0 strictEqual x
            0 + x strictEqual x
        }
    }

    "Identity with associativity" {
        MathsContext.empty {
            x + (0 + x) strictNotEqual x + x
            x + 0 + x strictNotEqual x + x

            withRule { additiveIdentity }
            withRule { additiveAssociativity }

            x + (0 + x) strictEqual x + x
            x + 0 + x strictEqual x + x

            x + 0 + 0 strictEqual x
            x + (0.c + 0.c) strictEqual x
            x + (1.c + 0.c) strictEqual x + 1
            x + 0 + x strictEqual x + x

            0 + x strictNotEqual x // no commutativity yet and identity rule is defined as right sided
        }
    }

    "Weird edge case with identity and associativity" {
        // With associativity and identity together we get an interest collapsing behaviour.
        // (x + 0) + x becomes x + x with identity
        // (x + 0) + x becomes x + (0 + x) with associativity
        // the transitivity here means that x + (0 + x) == x + x
        // despite the fact that the identity rule is only a right identity by the rule without commutativity.
        // Another peculiar part of this is that the associativity rule searches for the LHS and then creates the RHS.
        // If we were to remove the (x + 0) + x and directly equate the following:
        // x + (0 + x) == x + x
        // it would fail as it never had the opportunity to find the intermediate representation that bridges the expressions.
        // if the associativity rule took into account both sides then it would find this representation automatically.
        // I think that the way forward here is to have equality based rules be bidirectional.
        // If something should be in only one direction then that should be a different type of rule. Perhaps "implies".

        MathsContext.empty {
            val impliesAdditiveAssociativity by x + y + z implies x + (y + z)
            val impliesAdditiveIdentity by x + 0 implies x

            withRule { impliesAdditiveIdentity }
            withRule { impliesAdditiveAssociativity }

            x + (0 + x) strictNotEqual x + x
            x + 0 + x strictEqual x + (0 + x)
            x + (0 + x) strictEqual x + x
        }
    }

    "Associativity with commutativity" {
        MathsContext.empty {
            x + y + z strictNotEqual x + (y + z)

            withRule { additiveAssociativity }
            withRule { additiveCommutativity }

            x + 0 strictNotEqual x

            x + y + z strictEqual x + (y + z)
            x + y + z strictEqual x + (z + y)
            x + y + z strictEqual (z + y) + x
            x + y + z strictEqual (y + z) + x
            x + y + z strictEqual (x + y) + z
            x + y + z strictEqual (y + x) + z
            x + y + z strictEqual z + (y + x)
            x + y + z strictEqual y + (z + x)
            x + y + z strictEqual y + (x + z)
            x + y + z strictEqual (x + z) + y
            x + y + z strictEqual (z + x) + y
        }
    }

    "Identity, associativity, commutativity" {
        MathsContext.empty {
            0 + x strictNotEqual x
            x + (0 + x) strictNotEqual x + x
            x + 0 + x strictNotEqual x + x

            withRule { additiveIdentity }
            withRule { additiveAssociativity }
            withRule { additiveCommutativity }

            x + (0 + x) strictEqual x + x
            x + 0 + x strictEqual x + x

            x + 0 + 0 strictEqual x
            x + (0.c + 0.c) strictEqual x
            x + (1.c + 0.c) strictEqual x + 1
            x + 0 + x strictEqual x + x
            8.c + 0 strictEqual 8.c

            0 + x strictEqual x // equal now that commutativity is available

            x + y + z + 0 strictEqual x + (y + z)
            x + y + z strictEqual x + (z + y)
            x + y + z strictEqual (z + y) + x
            x + 0 + y + z strictEqual (y + z) + x
            x + y + z strictEqual (x + y) + z
            x + y + z strictEqual (y + x) + z + 0
            x + y + z strictEqual z + (0 + y + x)
            x + y + z strictEqual y + (z + 0 + x)
            0 + x + y + z strictEqual y + 0 + (x + z)
            x + y + z strictEqual (x + z) + y
            x + y + z strictEqual (z + x) + y
        }
    }
})