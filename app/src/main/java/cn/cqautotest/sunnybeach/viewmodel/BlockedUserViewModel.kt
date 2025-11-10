package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import cn.cqautotest.sunnybeach.model.LoadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/10
 * desc   : 黑名单用户 ViewModel
 */
class BlockedUserViewModel : ViewModel() {

    private val _blockLoadStatusFlow = MutableStateFlow<LoadStatus>(LoadStatus.Loading)
    val blockLoadStatusFlow: StateFlow<LoadStatus> get() = _blockLoadStatusFlow

    fun setUserBlockLoadStatus(loadStatus: LoadStatus) {
        _blockLoadStatusFlow.value = loadStatus
    }
}