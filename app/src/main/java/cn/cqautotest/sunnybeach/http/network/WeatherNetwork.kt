package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.http.api.weather.PlaceApi
import cn.cqautotest.sunnybeach.http.api.weather.WeatherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/08
 *    desc   : 天气获取
 */
object WeatherNetwork {

    private val mWeatherToken = AppApplication.getWeatherApiToken()

    suspend fun searchPlace(query: String) = PlaceApi.searchPlace(mWeatherToken, query).await()

    suspend fun getDailyWeather(lng: String, lat: String) = WeatherApi.getDailyWeather(mWeatherToken, lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = WeatherApi.getRealtimeWeather(mWeatherToken, lng, lat).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    t.printStackTrace()
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response body is null check the internet wrong"))
                    }
                }
            })
        }
    }
}