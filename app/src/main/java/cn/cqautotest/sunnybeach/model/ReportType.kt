package cn.cqautotest.sunnybeach.model

/**
 * 举报的枚举类型
 */
enum class ReportType(val title: String, val type: Int) {

    ARTICLE("文章", 1),
    QA("问答", 2),
    FISH("动态", 3),
    SHARE("分享", 4),
    ARTICLE_COMMENT("文章评论", 5),
    QA_COMMENT("问答评论", 6),
    FISH_COMMENT("动态评论", 7),
    USER("用户", 8),
}