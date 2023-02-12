package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/11/21
 * desc   : 问题搜索条件
 */
data class QuestionSearchFilter(
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("isResolve")
    val isResolve: String,
    @SerializedName("titleKeyword")
    val titleKeyword: String
)