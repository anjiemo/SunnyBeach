package cn.cqautotest.sunnybeach.http.api.weather

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.annotation.baseurl.CaiYunBaseUrl
import cn.cqautotest.sunnybeach.model.weather.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@CaiYunBaseUrl
interface PlaceApi {

    @GET("v2/place?lang=zh_CN")
    fun searchPlace(
        @Query("token") token: String,
        @Query("query") query: String
    ): Call<PlaceResponse>

    companion object : PlaceApi by ServiceCreator.create()
}