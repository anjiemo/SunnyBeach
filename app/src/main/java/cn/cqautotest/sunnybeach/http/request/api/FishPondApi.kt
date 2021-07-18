package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface FishPondApi {

    /**
     * 获取动态评论
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/comment/{momentId}/{page}?sort=1")
    suspend fun loadFishPondCommentListById(
        @Path("momentId") momentId: String,
        @Path("page") page: Long
    ): BaseResponse<FishPondComment>

    /**
     * 获取动态列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/list/{topicId}/{page}")
    suspend fun loadFishPondListById(
        @Path("topicId") topicId: String,
        @Path("page") page: Long
    ): BaseResponse<Fish>

    /**
     * 获取首页话题（类似于摸鱼首页侧栏）
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic/index")
    suspend fun loadTopicListByIndex(): BaseResponse<FishPondTopicIndex>

    /**
     * 获取热门动态列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/hot/{size}")
    suspend fun loadHotFish(@Path("size") size: Int): BaseResponse<Fish>

    /**
     * 获取话题列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic")
    suspend fun loadTopicList(): BaseResponse<FishPondTopicList>
}