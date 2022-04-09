package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.model.msg.IEDetail
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.*

interface UserApi {

    /**
     * 找回密码（通过短信找回）
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}uc/user/forget/{smsCode}")
    suspend fun modifyPasswordBySms(
        @Path("smsCode") smsCode: String,
        @Body user: User
    ): ApiResponse<Any>

    /**
     * 修改密码（通过旧密码修改）
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}uc/user/modify-pwd")
    suspend fun modifyPasswordByOldPwd(@Body modifyPwd: ModifyPwd): ApiResponse<Any>

    /**
     * 检查手机验证码是否正确
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/ut/check-sms-code/{phoneNumber}/{smsCode}")
    suspend fun checkSmsCode(
        @Path("phoneNumber") phoneNumber: String,
        @Path("smsCode") smsCode: String
    ): ApiResponse<Any>

    /**
     * 获取找回密码的手机验证码（找回密码）
     */
    @POST("${SUNNY_BEACH_API_BASE_URL}uc/ut/forget/send-sms")
    suspend fun sendForgetSmsVerifyCode(@Body smsInfo: SmsInfo): ApiResponse<Any>

    /**
     * 注册账号
     */
    @POST("${SUNNY_BEACH_API_BASE_URL}uc/user/register/{smsCode}")
    suspend fun registerAccount(
        @Path("smsCode") smsCode: String,
        @Body user: User
    ): ApiResponse<Any>

    /**
     * 获取注册的手机验证码（注册）
     */
    @POST("${SUNNY_BEACH_API_BASE_URL}uc/ut/join/send-sms")
    suspend fun sendRegisterSmsVerifyCode(@Body smsInfo: SmsInfo): ApiResponse<Any>

    /**
     * 获取用户 Sob 币的收支（Income & Expenditures）明细列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ast/ucenter/sob-trade/{userId}/{page}")
    suspend fun getSobIEDetailList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<IEDetail>

    /**
     * 获取VIP列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/vip/list")
    suspend fun getVipUserList(): ApiResponse<List<VipUserInfoSummary>>

    /**
     * 个人中心获取成就
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ast/ucenter/achievement")
    suspend fun getAchievement(): ApiResponse<UserAchievement>

    /**
     * 个人中心获取自己的sob币总数
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ast/ucenter/total-sob")
    suspend fun queryTotalSobCount(): ApiResponse<Int>

    /**
     * 个人中心获取账号信息
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/ucenter/user-info")
    suspend fun queryUserInfo(): ApiResponse<PersonCenterInfo>

    /**
     * 根据手机号查询用户头像
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/user/avatar/{phoneNum}")
    suspend fun queryUserAvatar(@Path("phoneNum") phoneNum: String): ApiResponse<String>

    /**
     * 获取富豪榜
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ast/rank/sob/{count}")
    suspend fun getRichList(@Path("count") count: Int): ApiResponse<RichList>

    /**
     * 退出登录
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/user/logout")
    suspend fun logout(): ApiResponse<Any>

    /**
     * 登录账号
     */
    @POST("${SUNNY_BEACH_API_BASE_URL}uc/user/login/{captcha}")
    suspend fun login(@Path("captcha") captcha: String, @Body user: User): ApiResponse<Any?>

    /**
     * 解析当前用户的 Token
     * Token 的有效期为 7天
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/user/checkToken")
    suspend fun checkToken(): ApiResponse<UserBasicInfo>

    /**
     * 获取指定用户成就，如果 userId 为 "" 则获取当前用户的成就
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ast/achievement/{userId}")
    suspend fun getAchievement(@Path("userId") userId: String): ApiResponse<UserAchievement>

    /**
     * 获取指定用户的信息
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/user-info/{userId}")
    suspend fun getUserInfo(@Path("userId") userId: String): ApiResponse<UserInfo>

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/fans/state/{userId}")
    suspend fun followState(@Path("userId") userId: String): ApiResponse<Int>

    /**
     * 关注用户
     */
    @POST("${SUNNY_BEACH_API_BASE_URL}uc/fans/{userId}")
    suspend fun followUser(@Path("userId") userId: String): ApiResponse<Any>

    /**
     * 取消关注用户
     */
    @DELETE("${SUNNY_BEACH_API_BASE_URL}uc/fans/{userId}")
    suspend fun unfollowUser(@Path("userId") userId: String): ApiResponse<Any>

    /**
     * 检查是否领取过津贴
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ast/vip-allowance")
    suspend fun checkAllowance(): ApiResponse<Any>

    /**
     * VIP领取津贴（每月）
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}ast/vip-allowance")
    suspend fun getAllowance(): ApiResponse<Any>

    /**
     * 获取用户关注的用户列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/follow/list/{userId}/{page}")
    suspend fun getUserFollowList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>

    /**
     * 获取用户的粉丝列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/fans/list/{userId}/{page}")
    suspend fun getUserFansList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>
}