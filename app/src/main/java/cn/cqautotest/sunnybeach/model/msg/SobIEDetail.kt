package cn.cqautotest.sunnybeach.model.msg

import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.annotation.SmartTable
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
    @SmartTable(name = "我的 sunof 币收支明细")
    data class Content(
        @SmartColumn(id = 2, name = "变化原因")
        @SerializedName("content")
        val content: String,
        @SmartColumn(id = 0, name = "日期")
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SmartColumn(id = 1, name = "变化")
        @SerializedName("sob")
        val sob: Int,
        @SmartColumn(id = 3, name = "税")
        @SerializedName("tax")
        val tax: Int,
        @SerializedName("updateTime")
        val updateTime: String,
        @SerializedName("userId")
        val userId: String
    )
}