package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import android.webkit.CookieManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.manager.CookieStore
import kotlinx.coroutines.launch

class CookiesViewModel(application: Application) : AndroidViewModel(application) {

    private val cookieDao = AppApplication.getDatabase().cookieDao()

    /**
     * 保存 Cookies
     */
    fun save(cookieStoreSet: List<CookieStore>) = viewModelScope.launch {
        // 把 Cookies 保存到存储 Cookie 的 Room 数据库中
        cookieDao.save(cookieStoreSet)
        // 将 Cookie 同步到 WebView 中
        val cm = CookieManager.getInstance()
        cm.run {
            for (cookieStore in cookieStoreSet) {
                for (cookie in cookieStore.cookies) {
                    setCookie(cookieStore.host, cookie.value)
                }
            }
            flush()
        }
    }

    fun getCookiesByHost(host: String) = cookieDao.getCookiesByHost(host)

    fun getCookies() = cookieDao.getCookies()
}