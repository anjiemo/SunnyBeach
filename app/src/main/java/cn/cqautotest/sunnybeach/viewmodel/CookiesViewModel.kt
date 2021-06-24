package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.manager.CookieStore
import kotlinx.coroutines.launch

class CookiesViewModel(application: Application) : AndroidViewModel(application) {

    private val cookieDao = AppApplication.getDatabase().cookieDao()

    fun save(cookieStoreSet: List<CookieStore>) = viewModelScope.launch {
        cookieDao.save(cookieStoreSet)
    }

    fun getCookiesByHost(host: String) = cookieDao.getCookiesByHost(host)

    fun getCookies() = cookieDao.getCookies()
}