package cn.cqautotest.sunnybeach.viewmodel.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import cn.cqautotest.sunnybeach.model.weather.Location
import cn.cqautotest.sunnybeach.repository.Repository

class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val weatherLiveData = locationLiveData.switchMap { input: Location ->
        Repository.refreshWeather(input.lng, input.lat)
    }

    fun refreshWeather(location: Location) {
        locationLiveData.value = location
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }
}