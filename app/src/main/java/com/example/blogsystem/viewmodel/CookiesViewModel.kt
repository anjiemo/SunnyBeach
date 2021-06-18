package com.example.blogsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blogsystem.base.App
import com.example.blogsystem.utils.CookieStore
import kotlinx.coroutines.launch

class CookiesViewModel(application: Application) : AndroidViewModel(application) {

    private val cookieDao = getApplication<App>().database.cookieDao()

    fun save(cookieStoreSet: List<CookieStore>) = viewModelScope.launch {
        cookieDao.save(cookieStoreSet)
    }

    fun getCookiesByHost(host: String) = cookieDao.getCookiesByHost(host)

    fun getCookies() = cookieDao.getCookies()
}