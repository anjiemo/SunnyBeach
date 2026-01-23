package cn.cqautotest.sunnybeach.repository

import cn.cqautotest.sunnybeach.model.aliyun.AliyunVideoUrlInfo
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : 视频存储库接口
 */
interface VideoRepository {

    /**
     * 获取视频播放详细信息
     */
    fun getVideoPlayInfo(videoId: String): Flow<Result<AliyunVideoUrlInfo>>
}
