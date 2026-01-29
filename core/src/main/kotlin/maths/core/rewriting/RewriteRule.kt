package maths.core.rewriting

import maths.core.verification.EMatchResult
import maths.core.verification.EMatcher
import maths.core.verification.RewriteResult

class RewriteRule(val pattern: EMatcher, val rewrite: (EMatchResult) -> RewriteResult?)
