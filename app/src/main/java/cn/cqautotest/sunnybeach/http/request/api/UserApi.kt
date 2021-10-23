package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.BaseResponse
import cn.cqautotest.sunnybeach.model.RichList
import cn.cqautotest.sunnybeach.model.User
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_BASE_URL
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @GET("${SUNNY_BEACH_BASE_URL}ast/rank/sob/{count}")
    suspend fun getRichList(@Path("count") count: Int): BaseResponse<RichList>

    @POST("${SUNNY_BEACH_BASE_URL}uc/user/login/{captcha}")
    suspend fun login(@Path("captcha") captcha: String, @Body user: User): BaseResponse<String>

    @GET("${SUNNY_BEACH_BASE_URL}uc/user/checkToken")
    suspend fun checkToken(): BaseResponse<UserBasicInfo>
}