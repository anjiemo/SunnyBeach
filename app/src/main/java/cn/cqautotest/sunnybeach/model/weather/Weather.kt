package cn.cqautotest.sunnyweather.logic.model

import cn.cqautotest.sunnybeach.model.weather.DailyResponse

data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)