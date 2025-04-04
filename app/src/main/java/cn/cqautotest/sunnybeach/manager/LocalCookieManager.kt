package cn.cqautotest.sunnybeach.manager

import android.webkit.CookieManager
import androidx.lifecycle.ViewModelProvider
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.util.StringUtil
import cn.cqautotest.sunnybeach.viewmodel.CookiesViewModel
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : Cookie 管理器
 */
class LocalCookieManager : CookieJar {

    private val cookiesViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(AppApplication.getInstance())
            .create(CookiesViewModel::class.java)
    }

    /**
     * 通过主机名获取保存的 cookie
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStore = cookiesViewModel.getCookiesByHost(url.host) ?: return emptyList()
        return cookieStore.cookies
    }

    /**
     * 保存该主机名下的 cookie
     */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookiesList = cookiesViewModel.getCookies()
        val cookiesSet = cookiesList.toMutableSet()
        val host = url.host
        cookiesSet.add(CookieStore(host = host, cookies = cookies))
        val cookieManager = CookieManager.getInstance()
        val cookie = cookies.toString()
        cookieManager.setCookie(host, cookie)
        Timber.d("===> host is $host cookie value is $cookie")
        cookiesViewModel.save(cookiesSet.toList())
    }

    companion object {

        private var sINSTANCE: LocalCookieManager? = null

        @JvmStatic
        fun get(): LocalCookieManager {
            return sINSTANCE ?: synchronized(this) {
                val instance = LocalCookieManager()
                sINSTANCE = instance
                instance
            }
        }
    }
}

@Entity(tableName = "tb_cookies")
data class CookieStore(
    @PrimaryKey val host: String,
    val domain: String = StringUtil.getTopDomain(host),
    val cookies: List<Cookie> = emptyList()
)
