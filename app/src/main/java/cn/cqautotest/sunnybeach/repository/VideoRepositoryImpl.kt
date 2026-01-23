package cn.cqautotest.sunnybeach.repository

import cn.cqautotest.sunnybeach.http.api.sob.AliyunVodApi
import cn.cqautotest.sunnybeach.http.api.sob.CourseApi
import cn.cqautotest.sunnybeach.model.aliyun.AliyunVideoUrlInfo
import cn.cqautotest.sunnybeach.util.AliyunVodSigner
import com.blankj.utilcode.util.EncodeUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/24
 * desc   : 视频存储库实现类
 */
@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val courseApi: CourseApi,
    private val aliyunVodApi: AliyunVodApi,
    private val gson: Gson
) : VideoRepository {

    override fun getVideoPlayInfo(videoId: String): Flow<Result<AliyunVideoUrlInfo>> = flow {
        try {
            // 获取播放凭证
            val certResponse = courseApi.getCoursePlayAuth(videoId)
            if (!certResponse.isSuccess()) {
                emit(Result.failure(Exception(certResponse.getMessage())))
                return@flow
            }
            val certData = certResponse.getData()
            val playAuth = certData.playAuth

            // 解析 PlayAuth (使用 AndroidUtilCode)
            val decodedAuthRaw = EncodeUtils.base64Decode(playAuth)
            val decodedAuth = String(decodedAuthRaw)
            val authJson = gson.fromJson(decodedAuth, JsonObject::class.java)

            val akId = authJson.get("AccessKeyId").asString
            val akSecret = authJson.get("AccessKeySecret").asString
            val token = authJson.get("SecurityToken").asString
            val authInfo = authJson.get("AuthInfo").asString

            // 计算签名并请求阿里云 (Aliyun 接口)
            val signedParams = AliyunVodSigner.sign(
                method = "GET",
                action = "GetPlayInfo",
                videoId = certData.videoId,
                authInfo = authInfo,
                accessKeyId = akId,
                accessKeySecret = akSecret,
                securityToken = token
            )

            val vodResponse = aliyunVodApi.getPlayInfo(signedParams)

            // 映射到领域模型
            val videoBase = vodResponse.videoBase
            val playInfoList = vodResponse.playInfoList?.playInfo ?: emptyList()

            val domainModel = AliyunVideoUrlInfo(
                videoId = videoBase?.videoId ?: certData.videoId,
                title = videoBase?.title ?: "",
                playUrls = playInfoList.map {
                    AliyunVideoUrlInfo.VideoUrl(
                        definition = it.definition ?: "Unknown",
                        url = it.playUrl ?: "",
                        format = it.format ?: ""
                    )
                }
            )
            emit(Result.success(domainModel))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
