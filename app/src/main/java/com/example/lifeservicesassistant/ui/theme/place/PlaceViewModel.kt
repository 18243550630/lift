package com.example.lifeservicesassistant.ui.theme.place

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.lifeservicesassistant.logic.Repository
import com.example.lifeservicesassistant.logic.dao.PlaceDao
import com.example.lifeservicesassistant.logic.model.Place
class PlaceViewModel : ViewModel() {

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()


    // 定义一个 MutableLiveData 用于存储 Place 列表数据
// 使用 MutableLiveData 存储最终结果
    private val searchLiveData = MutableLiveData<String>()

    val placeLiveData = MutableLiveData<List<Place>>()

    val placeList = ArrayList<Place>()

    fun searchPlaces(query: String) {
        searchLiveData.value = query
        searchLiveData.value?.let { query ->
            // 假设 Repository.searchPlaces 返回一个 LiveData<Result<List<Place>>>
            Repository.searchPlaces(query).observeForever { result ->
                // 检查 Result 是否成功并获取数据
                if (result.isSuccess) {
                    placeLiveData.value = result.getOrNull() // 获取成功的 List<Place>
                } else {
                    placeLiveData.value = emptyList() // 失败时设置为空列表或你希望的默认值
                }
            }
        }
    }

}