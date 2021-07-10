package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.BaseResponse
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondTopicIndex
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface FishPondApi {

    /**
     * 获取首页话题（类似于摸鱼首页侧栏）
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic/index")
    suspend fun loadTopicListByIndex() : BaseResponse<FishPondTopicIndex>

    /**
     * 获取热门动态列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/hot/{size}")
    suspend fun loadHotFish(@Path("size") size: Int): BaseResponse<Fish>

    /**
     * 获取话题列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic")
    suspend fun loadTopicList():BaseResponse<FishPondTopicList>
}