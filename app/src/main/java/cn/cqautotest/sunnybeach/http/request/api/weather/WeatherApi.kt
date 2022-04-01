package cn.cqautotest.sunnybeach.http.request.api.weather

import cn.cqautotest.sunnybeach.model.weather.DailyResponse
import cn.cqautotest.sunnybeach.model.weather.RealtimeResponse
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherApi {

    @GET("${CAI_YUN_BASE_URL}v2.5/{token}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(
        @Path("token") token: String,
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealtimeResponse>

    @GET("${CAI_YUN_BASE_URL}v2.5/{token}/{lng},{lat}/daily.json")
    fun getDailyWeather(
        @Path("token") token: String,
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<DailyResponse>
}