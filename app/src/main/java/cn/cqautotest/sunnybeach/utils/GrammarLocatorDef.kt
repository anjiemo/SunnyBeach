package cn.cqautotest.sunnybeach.utils

import cn.cqautotest.sunnybeach.utils.languages.Prism_c
import cn.cqautotest.sunnybeach.utils.languages.Prism_json
import cn.cqautotest.sunnybeach.utils.languages.Prism_kotlin
import cn.cqautotest.sunnybeach.utils.languages.Prism_python
import io.noties.prism4j.GrammarLocator
import io.noties.prism4j.Prism4j
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/27
 * desc   : Markdown 语法定位器
 */
class GrammarLocatorDef : GrammarLocator {
    override fun grammar(prism4j: Prism4j, language: String): Prism4j.Grammar? {
        return when (language.toLowerCase(Locale.ROOT)) {
            "java" -> Prism_json.create(prism4j)
            "kotlin" -> Prism_kotlin.create(prism4j)
            "python" -> Prism_python.create(prism4j)
            "json" -> Prism_json.create(prism4j)
            "c" -> Prism_c.create(prism4j)
            else -> null
        }
    }

    override fun languages(): MutableSet<String> {
        return mutableSetOf("java", "kotlin", "python", "json", "c")
    }
}