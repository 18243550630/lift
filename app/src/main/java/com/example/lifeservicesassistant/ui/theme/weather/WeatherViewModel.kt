package com.example.lifeservicesassistant.ui.theme.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lifeservicesassistant.logic.Repository
import com.example.lifeservicesassistant.logic.model.Location
import com.example.lifeservicesassistant.logic.model.Weather


class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    val weatherLiveData = MutableLiveData<Weather>() // 假设你的天气信息是 Weather 类型

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
        // 根据新方式，直接在 refreshWeather 中发起请求
        locationLiveData.value?.let { location ->
            // 假设 Repository.refreshWeather 返回 LiveData<Result<Weather>>
            Repository.refreshWeather(location.lng, location.lat).observeForever { result ->
                // 检查是否成功并更新 weatherLiveData
                if (result.isSuccess) {
                    weatherLiveData.value = result.getOrNull() // 设置成功的天气数据
                } else {
                    weatherLiveData.value = null // 如果失败，则清空数据
                }
            }
        }

    }
}
