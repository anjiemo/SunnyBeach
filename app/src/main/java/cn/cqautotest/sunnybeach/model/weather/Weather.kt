package cn.cqautotest.sunnybeach.model.weather

data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)