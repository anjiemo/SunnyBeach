package cn.cqautotest.sunnybeach.utils.markown

import io.noties.prism4j.annotations.PrismBundle

@PrismBundle(
    include = ["kotlin", "java"],
    grammarLocatorClassName = ".MyGrammarLocator"
)
open class GrammarLocatorFactory