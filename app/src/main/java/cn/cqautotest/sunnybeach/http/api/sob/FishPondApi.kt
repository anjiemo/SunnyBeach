package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface FishPondApi : ISobApi {

    /**
     * 获取指定用户的摸鱼动态列表
     * 如果当前未登录账号则只能获取到第一页的数据
     */
    @GET("ct/moyu/list/user/{userId}/{page}")
    suspend fun loadUserFishList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<List<Fish.FishItem>>

    /**
     * 上传图片（摸鱼动态）
     */
    @Multipart
    @POST("ct/image/mo_yu")
    suspend fun uploadFishImage(@Part part: MultipartBody.Part): ApiResponse<String>

    /**
     * 发布动态
     */
    @POST("ct/moyu")
    suspend fun putFish(@Body moment: @JvmSuppressWildcards Map<String, Any?>): ApiResponse<Any>

    /**
     * 获取动态评论
     */
    @GET("ct/moyu/comment/{momentId}/{page}?sort=1")
    suspend fun getFishCommendListById(
        @Path("momentId") momentId: String,
        @Path("page") page: Int
    ): ApiResponse<FishPondComment>

    /**
     * 获取动态详情
     */
    @GET("ct/moyu/{momentId}")
    suspend fun loadFishDetailById(@Path("momentId") momentId: String): ApiResponse<Fish.FishItem>

    /**
     * 获取动态列表
     */
    @GET("ct/moyu/list/{topicId}/{page}")
    suspend fun loadFishListById(
        @Path("topicId") topicId: String,
        @Path("page") page: Int
    ): ApiResponse<Fish>

    /**
     * 获取首页话题（类似于摸鱼首页侧栏）
     */
    @GET("ct/moyu/topic/index")
    suspend fun loadTopicListByIndex(): ApiResponse<FishPondTopicIndex>

    /**
     * 获取热门动态列表
     */
    @GET("ct/moyu/hot/{size}")
    suspend fun loadHotFish(@Path("size") size: Int): ApiResponse<Fish>

    /**
     * 获取话题列表
     */
    @GET("ct/moyu/topic")
    suspend fun loadTopicList(): ApiResponse<FishPondTopicList>

    /**
     * 发表评论(评论动态)
     */
    @POST("ct/moyu/comment")
    suspend fun postComment(@Body momentComment: @JvmSuppressWildcards Map<String, Any?>): ApiResponse<String>

    /**
     * 回复评论（回复动态列表下的评论）
     */
    @POST("ct/moyu/sub-comment")
    suspend fun replyComment(@Body momentComment: @JvmSuppressWildcards Map<String, Any?>): ApiResponse<String>

    /**
     * 动态点赞
     */
    @PUT("ct/moyu/thumb-up/{momentId}")
    suspend fun dynamicLikes(@Path("momentId") momentId: String): ApiResponse<Any>
}