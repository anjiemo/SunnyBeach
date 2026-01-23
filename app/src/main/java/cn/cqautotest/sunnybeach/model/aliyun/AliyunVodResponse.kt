package cn.cqautotest.sunnybeach.model.aliyun

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : 阿里云 VOD GetPlayInfo 响应模型
 */
data class AliyunVodResponse(
    @SerializedName("PlayInfoList") val playInfoList: PlayInfoList? = null,
    @SerializedName("VideoBase") val videoBase: VideoBase? = null,
    @SerializedName("RequestId") val requestId: String? = null
) {
    data class PlayInfoList(
        @SerializedName("PlayInfo") val playInfo: List<PlayInfo>? = null
    )

    data class PlayInfo(
        @SerializedName("Definition") val definition: String? = null,
        @SerializedName("PlayURL") val playUrl: String? = null,
        @SerializedName("Format") val format: String? = null,
        @SerializedName("Size") val size: Long? = 0,
        @SerializedName("Duration") val duration: String? = null,
        @SerializedName("Height") val height: Int? = 0,
        @SerializedName("Width") val width: Int? = 0
    )

    data class VideoBase(
        @SerializedName("VideoId") val videoId: String? = null,
        @SerializedName("Title") val title: String? = null,
        @SerializedName("CoverURL") val coverUrl: String? = null,
        @SerializedName("Duration") val duration: String? = null
    )
}
