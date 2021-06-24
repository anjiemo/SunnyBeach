package cn.cqautotest.sunnybeach.http.response.model

import com.google.gson.annotations.SerializedName

data class HomeBannerBean(
    @SerializedName("consume")
    val consume: String,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("errmsg")
    val errmsg: String,
    @SerializedName("errno")
    val errno: String,
    @SerializedName("total")
    val total: String
) {
    data class Data(
        @SerializedName("ad_id")
        val adId: String,
        @SerializedName("ad_img")
        val adImg: String,
        @SerializedName("ad_pos")
        val adPos: String,
        @SerializedName("ad_url")
        val adUrl: String,
        @SerializedName("class_id")
        val classId: String,
        @SerializedName("create_time")
        val createTime: String,
        @SerializedName("download_times")
        val downloadTimes: String,
        @SerializedName("ext_1")
        val ext1: String,
        @SerializedName("ext_2")
        val ext2: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("img_1024_768")
        val img1024768: String,
        @SerializedName("img_1280_1024")
        val img12801024: String,
        @SerializedName("img_1280_800")
        val img1280800: String,
        @SerializedName("img_1366_768")
        val img1366768: String,
        @SerializedName("img_1440_900")
        val img1440900: String,
        @SerializedName("img_1600_900")
        val img1600900: String,
        @SerializedName("imgcut")
        val imgcut: String,
        @SerializedName("rdata")
        val rdata: List<Any>,
        @SerializedName("resolution")
        val resolution: String,
        @SerializedName("tag")
        val tag: String,
        @SerializedName("tempdata")
        val tempdata: String,
        @SerializedName("update_time")
        val updateTime: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("url_mid")
        val urlMid: String,
        @SerializedName("url_mobile")
        val urlMobile: String,
        @SerializedName("url_thumb")
        val urlThumb: String,
        @SerializedName("utag")
        val utag: String
    )
}