package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.BaseResponse
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.Topic
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface FishPondApi {

    /**
     * 获取热门动态列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/hot/{size}")
    suspend fun loadHotFish(@Path("size") size: Int): BaseResponse<Fish>

    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic")
    suspend fun loadTopic():BaseResponse<Topic>
}