package cn.cqautotest.sunnybeach.util

import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.db.SobCacheManager
import cn.cqautotest.sunnybeach.db.dao.CookieDao
import cn.cqautotest.sunnybeach.manager.CookieStore
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import com.blankj.utilcode.util.ResourceUtils
import okhttp3.Cookie
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/19
 * desc   : WebView Hook 帮助类，可根据 url 注入 cookie 和 自适应屏幕
 */
object WebViewHookHelper {

    private val mDatabase = AppApplication.getDatabase()
    private val mCookieDao: CookieDao = mDatabase.cookieDao()

    private val mHookJs by lazy { ResourceUtils.readAssets2String("js/hook.js") }
    private val mFixSobSiteJs by lazy { ResourceUtils.readAssets2String("js/fix_sob_site.js") }

    /**
     * 注入 cookie
     */
    fun injectCookie(url: String?) {
        if (url.isNullOrEmpty()) return
        val domain: String = StringUtil.getTopDomain(SUNNY_BEACH_API_BASE_URL)
        val cookieManager: CookieManager = CookieManager.getInstance()
        val manager = ThreadPoolManager.getInstance()
        manager.execute {
            Timber.d("injectCookie：===> domain is %s", domain)
            val cookieStore: CookieStore? = mCookieDao.getCookiesByDomain(domain)
            if (cookieStore != null) {
                val cookieStoreList: List<Cookie> = cookieStore.cookies
                for (cookie in cookieStoreList) {
                    val cookieName = cookie.name
                    val cookieValue = cookie.value
                    val cookieDomain = cookie.domain
                    val cookieStr: String = buildCookie(cookieName, cookieValue, cookieDomain)
                    Timber.d("injectCookie：===> Set-Cookie is %s", cookieStr)
                    cookieManager.setCookie(url, cookieStr)
                }
                val newCookie: String = cookieManager.getCookie(url)
                Timber.d("injectCookie：===> newCookie is %s", newCookie)
            }
            val currUrlTopDomain: String = StringUtil.getTopDomain(url)
            val apiTopDomain: String = StringUtil.getTopDomain(SUNNY_BEACH_API_BASE_URL)
            val siteTopDomain: String = StringUtil.getTopDomain(SUNNY_BEACH_SITE_BASE_URL)
            if (currUrlTopDomain == apiTopDomain || currUrlTopDomain == siteTopDomain) {
                val cookieName = SobCacheManager.SOB_TOKEN_NAME
                val cookieValue = SobCacheManager.getSobToken()
                val apiCookie: String = buildCookie(cookieName, cookieValue, apiTopDomain)
                val siteCookie: String = buildCookie(cookieName, cookieValue, siteTopDomain)
                Timber.d("injectCookie：===> Set-Cookie：apiCookie is %s", apiCookie)
                Timber.d("injectCookie：===> Set-Cookie：siteCookie is %s", siteCookie)
                cookieManager.setCookie(url, apiCookie)
                cookieManager.setCookie(url, siteCookie)
            }
            Timber.d("injectCookie：===> CookieManager is finish")
        }
    }

    /**
     * 创建 cookie 字符串
     */
    private fun buildCookie(cookieName: String, cookieValue: String, siteTopDomain: String) =
        Cookie.Builder().name(cookieName)
            .value(cookieValue)
            .domain(siteTopDomain)
            .path("/")
            .build()
            .toString()

    /**
     * 通过 url 判断是否需要隐藏 dom 元素
     * view 参数不应该被保存起来，否则会导致内存泄漏
     */
    fun fitScreen(view: WebView?, url: String?) {
        view ?: return
        url ?: return
        takeIf { url.contains(SUNNY_BEACH_ARTICLE_URL_PRE) || url.contains(SUNNY_BEACH_QA_URL_PRE) }?.let { view.fitScreen() }
    }

    private fun WebView.fitScreen() {
        settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            textZoom = 175
        }
        setInitialScale(144)

        // 注入 hook.js
        evaluateJavascript(mHookJs, null)
        // 注入 fix_sob_site.js
        evaluateJavascript(mFixSobSiteJs, null)
    }
}