package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/11/21
 * desc   : 文章搜索条件
 */
data class ArticleSearchFilter(
    @SerializedName("categoryId")
    val categoryId: String = "",
    @SerializedName("state")
    val state: String = ArticleState.ALL.state,
    @SerializedName("titleKeyword")
    val titleKeyword: String = ""
) {

    enum class ArticleState(val typeName: String, val state: String) {

        ALL("全部", ""),
        PUBLISHED("已发布", "0"),
        UNDER_REVIEW("审核中", "1"),
        DRAFT("草稿", "2"),
        FAILED("未通过", "4");

        companion object {

            fun valueOfSate(state: String? = ""): ArticleState {
                return when (state) {
                    ALL.state -> ALL
                    PUBLISHED.state -> PUBLISHED
                    UNDER_REVIEW.state -> UNDER_REVIEW
                    DRAFT.state -> DRAFT
                    FAILED.state -> FAILED
                    else -> ALL
                }
            }
        }
    }
}