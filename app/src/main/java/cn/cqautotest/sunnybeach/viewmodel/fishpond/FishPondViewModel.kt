package cn.cqautotest.sunnybeach.viewmodel.fishpond

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.FishPondApi
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.Topic
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_HTTP_OK_CODE
import cn.cqautotest.sunnybeach.utils.logByDebug
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/10
 * desc   : 摸鱼列表的 ViewModel
 */
class FishPondViewModel : ViewModel() {

    private val fishPondApi by lazy { ServiceCreator.create<FishPondApi>() }

    // 摸鱼
    private val _fishPond = MutableLiveData<Fish?>()
    val fishPond: LiveData<Fish?> get() = _fishPond
    // 摸鱼话题列表
    private val _fishPondTopic = MutableLiveData<Topic?>()
    val fishTopic: LiveData<Topic?> get() = _fishPondTopic

    fun loadTopic() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            fishPondApi.loadTopic()
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPondTopic.value = responseData
            }
        }.onFailure {

        }
    }

    fun loadHotFish() = viewModelScope.launch {
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            fishPondApi.loadHotFish(10)
        }.onSuccess { response ->
            val responseData = response.data
            if (SUNNY_BEACH_HTTP_OK_CODE == response.code) {
                _fishPond.value = responseData
            }
        }.onFailure {

        }
    }
}