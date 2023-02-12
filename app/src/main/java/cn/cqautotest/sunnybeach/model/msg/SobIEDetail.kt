package cn.cqautotest.sunnybeach.model.msg

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/14
 * desc   : 收支（Income & Expenditures）明细
 */
data class SobIEDetail(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPre")
    val hasPre: Boolean,
    @SerializedName("list")
    val list: List<Content>,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
    data class Content(
        @SerializedName("content")
        val content: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("sob")
        val sob: Int,
        @SerializedName("tax")
        val tax: Int,
        @SerializedName("updateTime")
        val updateTime: String,
        @SerializedName("userId")
        val userId: String
    )
}