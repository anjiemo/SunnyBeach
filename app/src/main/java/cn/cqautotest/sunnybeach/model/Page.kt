package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

open class Page<T> {
    @SerializedName("currentPage")
    val currentPage: Int = 0

    @SerializedName("hasNext")
    val hasNext: Boolean = false

    @SerializedName("hasPre")
    val hasPre: Boolean = false

    @SerializedName("list")
    val list: List<T> = listOf()

    @SerializedName("pageSize")
    val pageSize: Int = 0

    @SerializedName("total")
    val total: Int = 0

    @SerializedName("totalPage")
    val totalPage: Int = 0
}