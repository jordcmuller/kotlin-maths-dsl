package maths.core.egraph.querying

import maths.core.egraph.EMatcher

interface QueryCondition

class PatternCondition(val pattern: EMatcher): QueryCondition

class EqualsCondition(val left: PatternCondition, val right: PatternCondition): QueryCondition
class NotEqualsCondition(val left: PatternCondition, val right: PatternCondition): QueryCondition

class AndCondition(val left: QueryCondition, val right: QueryCondition): QueryCondition
class OrCondition(val left: QueryCondition, val right: QueryCondition): QueryCondition
//class NotCondition(val base: QueryCondition): QueryCondition
