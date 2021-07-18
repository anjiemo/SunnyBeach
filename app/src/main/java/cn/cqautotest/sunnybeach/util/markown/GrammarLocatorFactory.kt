package cn.cqautotest.sunnybeach.util.markown

import io.noties.prism4j.annotations.PrismBundle

@PrismBundle(
    include = ["kotlin", "java"],
    grammarLocatorClassName = ".MyGrammarLocator"
)
open class GrammarLocatorFactory