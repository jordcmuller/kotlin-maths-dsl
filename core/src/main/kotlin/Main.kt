import maths.core.dsl.maths

fun main() {
    maths {
        val x by variable()
        val y by variable()
        
//        val a by x + y // TODO: consider this a bit more deeply

//        x + 1 eq x + 1
//        1.c eq 2.c
        
//        x eq 2.c
//        x eq 1.c
        
        y eq 1
        x eq y
        x eq 2
    }.printErrors()
}