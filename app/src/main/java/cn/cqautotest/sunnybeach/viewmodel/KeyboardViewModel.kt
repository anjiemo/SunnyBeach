package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/15
 * desc   : 键盘的 ViewModel
 */
class KeyboardViewModel : ViewModel() {

    private val _keyboardStateLiveData = MutableLiveData<Boolean>()
    val keyboardStateLiveData: LiveData<Boolean> get() = _keyboardStateLiveData
    private val _keyboardHeightLiveData = MutableLiveData<Int>()
    val keyboardHeightLiveData: LiveData<Int> get() = _keyboardHeightLiveData

    fun showKeyboard() {
        _keyboardStateLiveData.value = true
    }

    fun hideKeyboard() {
        _keyboardStateLiveData.value = false
    }

    fun toggleKeyboard() {
        val isShow = _keyboardStateLiveData.value ?: true
        _keyboardStateLiveData.value = !isShow
    }

    fun setKeyboardHeight(keyboardHeight: Int) {
        _keyboardHeightLiveData.value = keyboardHeight
    }
}