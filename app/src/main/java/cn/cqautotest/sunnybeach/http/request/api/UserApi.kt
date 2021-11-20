package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_BASE_URL
import retrofit2.http.*

interface UserApi {

    /**
     * 根据手机号查询用户头像
     */
    @GET("${SUNNY_BEACH_BASE_URL}uc/user/avatar/{phoneNum}")
    suspend fun queryUserAvatar(@Path("phoneNum") phoneNum: String): ApiResponse<String>

    /**
     * 获取富豪榜
     */
    @GET("${SUNNY_BEACH_BASE_URL}ast/rank/sob/{count}")
    suspend fun getRichList(@Path("count") count: Int): ApiResponse<RichList>

    /**
     * 登录账号
     */
    @POST("${SUNNY_BEACH_BASE_URL}uc/user/login/{captcha}")
    suspend fun login(@Path("captcha") captcha: String, @Body user: User): ApiResponse<String>

    /**
     * 解析当前用户的 Token
     */
    @GET("${SUNNY_BEACH_BASE_URL}uc/user/checkToken")
    suspend fun checkToken(): ApiResponse<UserBasicInfo>

    /**
     * 获取指定用户成就，如果 userId 为 "" 则获取当前用户的成就
     */
    @GET("${SUNNY_BEACH_BASE_URL}ast/achievement/{userId}")
    suspend fun getAchievement(@Path("userId") userId: String): ApiResponse<UserAchievement>

    /**
     * 获取指定用户的信息
     */
    @GET("${SUNNY_BEACH_BASE_URL}uc/user-info/{userId}")
    suspend fun getUserInfo(@Path("userId") userId: String): ApiResponse<UserInfo>

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    @GET("${SUNNY_BEACH_BASE_URL}uc/fans/state/{userId}")
    suspend fun followState(@Path("userId") userId: String): ApiResponse<Int>

    /**
     * 关注用户
     */
    @POST("${SUNNY_BEACH_BASE_URL}uc/fans/{userId}")
    suspend fun followUser(@Path("userId") userId: String): ApiResponse<Any>

    /**
     * 取消关注用户
     */
    @DELETE("${SUNNY_BEACH_BASE_URL}uc/fans/{userId}")
    suspend fun unfollowUser(@Path("userId") userId: String): ApiResponse<Any>

    /**
     * 检查是否领取过津贴
     */
    @GET("${SUNNY_BEACH_BASE_URL}ast/vip-allowance")
    suspend fun checkAllowance(): ApiResponse<Any>

    /**
     * VIP领取津贴（每月）
     */
    @PUT("${SUNNY_BEACH_BASE_URL}ast/vip-allowance")
    suspend fun getAllowance(): ApiResponse<Any>

    /**
     * 获取用户关注的用户列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}uc/follow/list/{userId}/{page}")
    suspend fun getUserFollowList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>

    /**
     * 获取用户的粉丝列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}uc/fans/list/{userId}/{page}")
    suspend fun getUserFansList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>
}