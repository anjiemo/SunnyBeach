package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/27
 * desc   : 首页 Activity 的 ViewModel
 */
class HomeViewModel : ViewModel() {

    private val _bottomNavigationHeightFlow = MutableStateFlow(0)
    val bottomNavigationHeightFlow: StateFlow<Int> get() = _bottomNavigationHeightFlow

    fun updateBottomNavigationHeight(height: Int) = viewModelScope.launch {
        _bottomNavigationHeightFlow.emit(height)
    }
}