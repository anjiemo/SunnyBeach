package com.example.blogsystem.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blogsystem.network.Repository
import com.example.blogsystem.network.model.music.LoginQrCheck
import com.example.blogsystem.network.model.music.LoginQrKey

class MusicViewModel : ViewModel() {

    private var mQrImg = ""
    private val _qrKeyLiveData = MutableLiveData<LoginQrKey>()
    val qrKeyLiveData: LiveData<LoginQrKey> get() = _qrKeyLiveData

    private val _qrBitmapLiveData = MutableLiveData<Bitmap>()
    val qrBitmapLiveData: LiveData<Bitmap> get() = _qrBitmapLiveData

    private val _qrStateLiveData = MutableLiveData<LoginQrCheck>()
    val qrStateLiveData: LiveData<LoginQrCheck> get() = _qrStateLiveData

    suspend fun refreshQrCodeKey() {
        try {
            _qrKeyLiveData.value = Repository.getLoginQrKey()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadQrBitmap(loginQrKey: LoginQrKey) {
        try {
            val uniKey = loginQrKey.data.unikey
            val loginQrCreate = Repository.createLoginQrCode(key = uniKey)
            val qrImg = loginQrCreate.data.qrimg.replace("data:image/png;base64,", "")
            // 如果和原来的二维码一致，则不需要重复解析
            if (mQrImg == qrImg) {
                return
            }
            mQrImg = qrImg
            val decodedString = Base64.decode(qrImg, Base64.DEFAULT)
            val qrCodeBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            _qrBitmapLiveData.value = qrCodeBitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun checkQrCodeState() {
        val uniKey = _qrKeyLiveData.value?.data?.unikey ?: return
        try {
            _qrStateLiveData.value = Repository.checkLoginQrCode(uniKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}