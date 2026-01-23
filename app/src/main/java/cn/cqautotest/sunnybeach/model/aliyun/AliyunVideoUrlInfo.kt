package cn.cqautotest.sunnybeach.model.aliyun

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : 阿里云视频播放信息
 */
data class AliyunVideoUrlInfo(
    val videoId: String,
    val title: String,
    val playUrls: List<VideoUrl>
) {
    data class VideoUrl(
        val definition: String,
        val url: String,
        val format: String
    )
}
