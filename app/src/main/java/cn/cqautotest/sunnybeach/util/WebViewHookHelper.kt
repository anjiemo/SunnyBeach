package cn.cqautotest.sunnybeach.util

import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.db.SobCacheManager
import cn.cqautotest.sunnybeach.db.dao.CookieDao
import cn.cqautotest.sunnybeach.manager.CookieStore
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import okhttp3.Cookie
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/19
 * desc   : WebView Hook 帮助类，可根据 url 注入 cookie 和 自适应屏幕
 */
object WebViewHookHelper {

    private val database = AppApplication.getDatabase()
    private val cookieDao: CookieDao = database.cookieDao()

    fun injectCookie(url: String?) {
        url ?: return
        val domain: String = StringUtil.getTopDomain(SUNNY_BEACH_API_BASE_URL)
        val manager = ThreadPoolManager.getInstance()
        manager.execute {
            Timber.d("hookUrlLoad：===> domain is %s", domain)
            val cookieManager: CookieManager = CookieManager.getInstance()
            val cookieStore: CookieStore? = cookieDao.getCookiesByDomain(domain)
            if (cookieStore != null) {
                val cookieStoreList: List<Cookie> = cookieStore.cookies
                for (cookie in cookieStoreList) {
                    val cookieName = cookie.name
                    val cookieValue = cookie.value
                    val cookieDomain = cookie.domain
                    val cookieStr: String = Cookie.Builder()
                        .name(cookieName)
                        .value(cookieValue)
                        .domain(cookieDomain)
                        .path("/")
                        .build()
                        .toString()
                    Timber.d("hookUrlLoad：===> Set-Cookie is %s", cookieStr)
                    cookieManager.setCookie(url, cookieStr)
                }
            }
            val newCookie: String = cookieManager.getCookie(url)
            Timber.d("hookUrlLoad：===> newCookie is %s", newCookie)
            val currUrlTopDomain: String = StringUtil.getTopDomain(url)
            val apiTopDomain: String = StringUtil.getTopDomain(SUNNY_BEACH_API_BASE_URL)
            val siteTopDomain: String = StringUtil.getTopDomain(SUNNY_BEACH_SITE_BASE_URL)
            if (currUrlTopDomain == apiTopDomain || currUrlTopDomain == siteTopDomain) {
                val cookieName = SobCacheManager.SOB_TOKEN_NAME
                val cookieValue = SobCacheManager.getSobToken()
                val apiCookie: String = Cookie.Builder()
                    .name(cookieName)
                    .value(cookieValue)
                    .domain(apiTopDomain)
                    .path("/")
                    .build()
                    .toString()
                val siteCookie: String = Cookie.Builder()
                    .name(cookieName)
                    .value(cookieValue)
                    .domain(siteTopDomain)
                    .path("/")
                    .build()
                    .toString()
                Timber.d("===> Set-Cookie：apiCookie is %s", apiCookie)
                Timber.d("===> Set-Cookie：siteCookie is %s", siteCookie)
                cookieManager.setCookie(url, apiCookie)
                cookieManager.setCookie(url, siteCookie)
            }
            Timber.d("===> CookieManager is finish")
        }
    }

    /**
     * 通过 url 判断是否需要隐藏 dom 元素
     * view 参数不应该被保存起来，否则会导致内存泄漏
     */
    fun fitScreen(view: WebView?, url: String?) {
        view ?: return
        url ?: return
        if (url.contains(SUNNY_BEACH_ARTICLE_URL_PRE) || url.contains(SUNNY_BEACH_QA_URL_PRE)) {
            view.fitScreen()
        }
    }

    private fun WebView.fitScreen() {
        settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            textZoom = 125
        }
        setInitialScale(144)
        // 隐藏 header
        evaluateJavascript(
            "var child=document.getElementById(\"header-container\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        // 隐藏文章详情界面相关
        evaluateJavascript(
            "var child=document.getElementById(\"article-detail-left-part\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById(\"article-detail-right-part\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById('article-detail-center-part');\n" +
                    "child.style.margin='-20px 0 0 0';", null
        )
        // 隐藏问答详情界面相关
        evaluateJavascript(
            "var child=document.getElementById('wenda-detail-left-part');\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById('wenda-detail-right-part');\n" +
                    "child.style.display=\"none\";\n", null
        )
        evaluateJavascript(
            "var child=document.getElementById('wenda-detail-center-part');\n" +
                    "child.style.margin='-20px 0 0 0';", null
        )
        // 隐藏 footer
        evaluateJavascript(
            "var child=document.getElementById(\"footer-container\");\n" +
                    "child.style.display=\"none\";\n", null
        )
        // 设置整体界面的宽度
        evaluateJavascript(
            "var child=document.getElementById('main-content');\n" +
                    "child.style.width='750px';", null
        )
    }
}