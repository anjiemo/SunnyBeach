package cn.cqautotest.sunnybeach.manager

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.cqautotest.sunnybeach.viewmodel.CookiesViewModel
import cn.cqautotest.sunnybeach.viewmodel.SingletonManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieManager : CookieJar {

    private val cookiesViewModel: CookiesViewModel by lazy {
        SingletonManager.cookiesViewModel
    }

    /**
     * 通过主机名获取保存的 cookie
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStore = cookiesViewModel.getCookiesByHost(url.host) ?: return listOf()
        return cookieStore.cookies.toList()
    }

    /**
     * 保存该主机名下的 cookie
     */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookiesList = cookiesViewModel.getCookies()
        val cookiesSet = cookiesList.toMutableSet()
        cookiesSet.add(CookieStore(url.host, cookies))
        cookiesViewModel.save(cookiesSet.toList())
    }
}

@Entity(tableName = "tb_cookies")
data class CookieStore(
    @PrimaryKey val host: String,
    val cookies: List<Cookie> = listOf()
)
