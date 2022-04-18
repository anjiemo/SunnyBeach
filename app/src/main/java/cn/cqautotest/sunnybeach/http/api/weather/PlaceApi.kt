package cn.cqautotest.sunnybeach.http.api.weather

import cn.cqautotest.sunnybeach.model.weather.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceApi : ICaiYunApi {

    @GET("v2/place?lang=zh_CN")
    fun searchPlace(
        @Query("token") token: String,
        @Query("query") query: String
    ): Call<PlaceResponse>
}