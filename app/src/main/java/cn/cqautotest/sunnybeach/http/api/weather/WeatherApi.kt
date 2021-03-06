package cn.cqautotest.sunnybeach.http.api.weather

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.annotation.baseurl.CaiYunBaseUrl
import cn.cqautotest.sunnybeach.model.weather.DailyResponse
import cn.cqautotest.sunnybeach.model.weather.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

@CaiYunBaseUrl
interface WeatherApi {

    @GET("v2.5/{token}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(
        @Path("token") token: String,
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealtimeResponse>

    @GET("v2.5/{token}/{lng},{lat}/daily.json")
    fun getDailyWeather(
        @Path("token") token: String,
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<DailyResponse>

    companion object : WeatherApi by ServiceCreator.create()
}