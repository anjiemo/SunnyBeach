package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.ProgressBar
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.db.SobCacheManager
import cn.cqautotest.sunnybeach.db.SobCacheManager.getSobToken
import cn.cqautotest.sunnybeach.db.dao.CookieDao
import cn.cqautotest.sunnybeach.manager.CookieStore
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import cn.cqautotest.sunnybeach.other.FitScreen
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.widget.BrowserView
import cn.cqautotest.sunnybeach.widget.BrowserView.BrowserChromeClient
import cn.cqautotest.sunnybeach.widget.BrowserView.BrowserViewClient
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.hjq.bar.TitleBar
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare.OnShareListener
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import okhttp3.Cookie
import okhttp3.FormBody
import timber.log.Timber


/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 浏览器界面
 */
class BrowserActivity : AppActivity(), StatusAction, OnRefreshListener {

    companion object {

        private const val INTENT_KEY_IN_URL: String = "url"

        private const val OPEN_ID: String = "id"
        private const val NICK_NAME = "nickName"
        private const val AVATAR_URL = "avatarUrl"

        // 是否加载为反馈界面
        private const val IS_FEED_BACK = "isFeedback"

        @CheckNet
        @Log
        fun start(
            context: Context,
            url: String,
            isFeedback: Boolean = false,
            openId: String = "",
            nickName: String = "",
            avatar: String = ""
        ) {
            if (TextUtils.isEmpty(url)) {
                return
            }
            val intent = Intent(context, BrowserActivity::class.java).apply {
                putExtra(INTENT_KEY_IN_URL, url)
                putExtra(IS_FEED_BACK, isFeedback)
                putExtra(OPEN_ID, openId)
                putExtra(NICK_NAME, nickName)
                putExtra(AVATAR_URL, avatar)
            }
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val hintLayout: StatusLayout? by lazy { findViewById(R.id.hl_browser_hint) }
    private val progressBar: ProgressBar? by lazy { findViewById(R.id.pb_browser_progress) }
    private val refreshLayout: SmartRefreshLayout? by lazy { findViewById(R.id.sl_browser_refresh) }
    private val browserView: BrowserView? by lazy { findViewById(R.id.wv_browser_view) }

    override fun getLayoutId(): Int {
        return R.layout.browser_activity
    }

    override fun initView() {
        // 设置 WebView 生命管控
        browserView?.setLifecycleOwner(this)
        // 设置网页刷新监听
        refreshLayout?.setOnRefreshListener(this)
    }

    override fun initData() {
        showLoading()
        browserView?.apply {
            setBrowserViewClient(AppBrowserViewClient())
            setBrowserChromeClient(AppBrowserChromeClient(this))
            val url = getString(INTENT_KEY_IN_URL)!!
            val isFeedback = getBoolean(IS_FEED_BACK)
            if (isFeedback) {
                val openId = getString(OPEN_ID)
                val nickName = getString(NICK_NAME)
                val avatar = getString(AVATAR_URL)
                Timber.d("initData：===> openId is $openId nickName is $nickName avatar is $avatar")
                val queryParams = FormBody.Builder()
                    .add("nickname", nickName ?: "游客")
                    .add("avatar", avatar ?: "")
                    .add("openid", openId ?: "")
                    .build().toQueryParams()
                postUrl(url, queryParams.toByteArray())
            } else {
                loadUrl(url)
            }
        }
    }

    private fun FormBody.toQueryParams(): String = buildString {
        repeat(size) {
            append(name(it))
            append("=")
            append(value(it))
            append("&")
        }
        if (size != 0) deleteAt(lastIndex)
    }

    override fun getStatusLayout(): StatusLayout? {
        return hintLayout
    }

    override fun onLeftClick(titleBar: TitleBar) {
        finish()
    }

    override fun onRightClick(titleBar: TitleBar) {
        val content = UMWeb(browserView?.url)
        content.title = browserView?.title
        content.setThumb(UMImage(this, R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(this)
            .setShareLink(content)
            .setListener(object : OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    toast("分享取消")
                }
            })
            .show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        browserView?.apply {
            if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
                // 后退网页并且拦截该事件
                goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 重新加载当前页
     */
    @CheckNet
    private fun reload() {
        browserView?.reload()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        reload()
    }

    override fun isStatusBarDarkFont(): Boolean {
        return false
    }

    private inner class AppBrowserViewClient : BrowserViewClient() {

        val appApplication = AppApplication.getDatabase()
        private val cookieDao: CookieDao = appApplication.cookieDao()

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        @Deprecated("Deprecated in Java")
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post {
                showError { reload() }
            }
        }

        /**
         * 开始加载网页
         */
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            progressBar?.visibility = View.VISIBLE
            injectCookie(url)
        }

        private fun injectCookie(url: String) {
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
                    val cookieValue = getSobToken()
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
         * 完成加载网页
         */
        override fun onPageFinished(view: WebView, url: String) {
            progressBar?.visibility = View.GONE
            if (url.contains(SUNNY_BEACH_ARTICLE_URL_PRE) || url.contains(SUNNY_BEACH_QA_URL_PRE)) {
                val fitScreen = FitScreen(view)
                fitScreen.run()
            }
            refreshLayout?.finishRefresh()
            showComplete()
        }
    }

    private inner class AppBrowserChromeClient constructor(view: BrowserView) : BrowserChromeClient(view) {

        /**
         * 收到网页标题
         */
        override fun onReceivedTitle(view: WebView, title: String?) {
            if (title == null) {
                return
            }
            setTitle(title)
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
            if (icon == null) {
                return
            }
            setRightIcon(BitmapDrawable(resources, icon))
        }

        /**
         * 收到加载进度变化
         */
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar?.progress = newProgress
        }
    }
}