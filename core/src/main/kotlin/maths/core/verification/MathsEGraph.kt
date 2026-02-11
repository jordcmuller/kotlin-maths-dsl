package maths.core.verification

import maths.core.ast.Expr

class MathsEGraph: EGraph<Expr>(MathsLowerer())