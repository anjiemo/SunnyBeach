package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.annotation.baseurl.SobBaseUrl
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserShare
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface ShareApi {

    /**
     * 获取指定用户的分享列表
     */
    @GET("ct/share/list/{userId}/{page}")
    suspend fun loadUserShareList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserShare>

    companion object : ShareApi by ServiceCreator.create()
}