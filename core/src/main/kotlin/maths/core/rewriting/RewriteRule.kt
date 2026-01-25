package maths.core.rewriting

import maths.core.ast.Expr
import maths.core.verification.EMatcher

class RewriteRule(val structure: EMatcher, val rewrite: (Expr) -> Expr?)
