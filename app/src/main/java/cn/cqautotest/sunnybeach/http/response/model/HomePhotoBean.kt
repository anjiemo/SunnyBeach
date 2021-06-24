package cn.cqautotest.sunnybeach.http.response.model

import com.google.gson.annotations.SerializedName

data class HomePhotoBean(
    @SerializedName("code")
    val code: Int,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("res")
    val res: Res
) {
    data class Res(
        @SerializedName("ads")
        val ads: List<Any>,
        @SerializedName("vertical")
        val vertical: List<Vertical>
    ) {
        data class Vertical(
            @SerializedName("atime")
            val atime: Int,
            @SerializedName("cid")
            val cid: List<String>,
            @SerializedName("cr")
            val cr: Boolean,
            @SerializedName("desc")
            val desc: String,
            @SerializedName("favs")
            val favs: Int,
            @SerializedName("id")
            val id: String,
            @SerializedName("img")
            val img: String,
            @SerializedName("ncos")
            val ncos: Int,
            @SerializedName("preview")
            val preview: String,
            @SerializedName("rank")
            val rank: Int,
            @SerializedName("rule")
            val rule: String,
            @SerializedName("source_type")
            val sourceType: String,
            @SerializedName("store")
            val store: String,
            @SerializedName("tag")
            val tag: List<Any>,
            @SerializedName("thumb")
            val thumb: String,
            @SerializedName("url")
            val url: List<Any>,
            @SerializedName("views")
            val views: Int,
            @SerializedName("wp")
            val wp: String,
            @SerializedName("xr")
            val xr: Boolean
        )
    }
}