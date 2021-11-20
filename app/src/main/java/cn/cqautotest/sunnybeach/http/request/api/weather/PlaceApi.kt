package cn.cqautotest.sunnybeach.http.request.api.weather

import cn.cqautotest.sunnybeach.model.weather.PlaceResponse
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceApi {

    @GET("${CAI_YUN_BASE_URL}v2/place?lang=zh_CN")
    fun searchPlace(
        @Query("token") token: String,
        @Query("query") query: String
    ): Call<PlaceResponse>
}