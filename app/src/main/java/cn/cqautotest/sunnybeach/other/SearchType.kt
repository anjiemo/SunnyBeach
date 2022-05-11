package cn.cqautotest.sunnybeach.other

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 搜索类型
 */
enum class SearchType(val value: String) {

    // 全部
    ALL(""),

    // 文章
    ARTICLE("a"),

    // 问答
    QA("w"),

    // 分享
    SHARE("s");

    companion object {

        fun valueOfType(value: String) = when (value) {
            "a" -> ARTICLE
            "w" -> QA
            "s" -> SHARE
            else -> ALL
        }
    }
}