package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.webkit.ConsoleMessage
import android.webkit.WebView
import android.widget.ProgressBar
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_FISH_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SHARE_URL_PRE
import cn.cqautotest.sunnybeach.util.WebViewHookHelper
import cn.cqautotest.sunnybeach.widget.BrowserView
import cn.cqautotest.sunnybeach.widget.BrowserView.BrowserChromeClient
import cn.cqautotest.sunnybeach.widget.BrowserView.BrowserViewClient
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.flyjingfish.android_aop_core.annotations.CheckNetwork
import com.hjq.bar.TitleBar
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare.OnShareListener
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import timber.log.Timber
import kotlin.math.abs

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

        @CheckNetwork(invokeListener = true)
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
            context.startActivity<BrowserActivity> {
                putExtra(INTENT_KEY_IN_URL, url)
                putExtra(IS_FEED_BACK, isFeedback)
                putExtra(OPEN_ID, openId)
                putExtra(NICK_NAME, nickName)
                putExtra(AVATAR_URL, avatar)
            }
        }
    }

    private val hintLayout: StatusLayout? by lazy { findViewById(R.id.hl_browser_hint) }
    private val progressBar: ProgressBar? by lazy { findViewById(R.id.pb_browser_progress) }
    private val refreshLayout: SmartRefreshLayout? by lazy { findViewById(R.id.sl_browser_refresh) }
    private val browserView: BrowserView? by lazy { findViewById(R.id.wv_browser_view) }

    override fun getLayoutId(): Int = R.layout.browser_activity

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
            val newUrl = if (isFeedback) {
                val openId = getString(OPEN_ID)
                val nickName = getString(NICK_NAME)
                val avatar = getString(AVATAR_URL)
                Timber.d("initData：===> openId is $openId nickName is $nickName avatar is $avatar")
                Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("nickname", nickName ?: "游客")
                    .appendQueryParameter("avatar", avatar.orEmpty())
                    .appendQueryParameter("openid", openId.orEmpty())
                    .toString()
            } else {
                url
            }
            Timber.d("initData：===> newUrl is $newUrl")
            loadUrl(newUrl)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        val clickTap = ViewConfiguration.getDoubleTapTimeout()
        // 最低滑动距离
        val minDist = ViewConfiguration.get(this).scaledTouchSlop
        var lastClickTime = 0L
        var x = 0f
        var y = 0f
        browserView?.setOnTouchListener { _, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    lastClickTime = System.currentTimeMillis()
                    x = event.x
                    y = event.y
                    false
                }

                MotionEvent.ACTION_UP -> {
                    val singClick = System.currentTimeMillis() - lastClickTime < clickTap
                    val xDist = abs(event.x - x)
                    val yDist = abs(event.y - y)
                    takeIf { interceptResClick() && singClick && xDist < minDist && yDist < minDist }?.let { handleResClick() } ?: false
                }

                else -> false
            }
        }
    }

    /**
     * 是否拦截处理
     */
    private fun interceptResClick(): Boolean {
        val curUrl = browserView?.url ?: return false
        return when {
            curUrl.startsWith(SUNNY_BEACH_ARTICLE_URL_PRE) -> true
            curUrl.startsWith(SUNNY_BEACH_FISH_URL_PRE) -> true
            curUrl.startsWith(SUNNY_BEACH_QA_URL_PRE) -> true
            curUrl.startsWith(SUNNY_BEACH_SHARE_URL_PRE) -> true
            else -> false
        }
    }

    /**
     * 处理资源被点击的情况
     */
    private fun handleResClick(): Boolean {
        val hitTestResult = browserView?.hitTestResult ?: return false
        return when (hitTestResult.type) {
            WebView.HitTestResult.IMAGE_TYPE, WebView.HitTestResult.IMAGE_ANCHOR_TYPE -> {
                try {
                    handleImage(hitTestResult.extra)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    // 处理图片错误，不支持查看的图片类型（可能是 base64 的图片，图片过大）
                    return false
                }
                true
            }

            else -> false
        }
    }

    /**
     * 处理图片被单击的情况
     */
    private fun handleImage(imageLink: String?) {
        imageLink?.let { ImagePreviewActivity.start(this, it) }
    }

    override fun getStatusLayout(): StatusLayout? = hintLayout

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

    override fun onBackPressed() {
        browserView.takeIf { it != null && it.canGoBack() }?.goBack() ?: super.onBackPressed()
    }

    /**
     * 重新加载当前页
     */
    @CheckNetwork(invokeListener = true)
    private fun reload() {
        browserView?.reload()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        reload()
    }

    override fun isStatusBarDarkFont() = false

    private inner class AppBrowserViewClient : BrowserViewClient() {

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Timber.d("onReceivedError：===> errorCode is $errorCode description is $description failingUrl is $failingUrl")
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
            // 给指定的 url 注入 cookie
            WebViewHookHelper.injectCookie(url)
        }

        /**
         * 完成加载网页
         */
        override fun onPageFinished(view: WebView, url: String) {
            progressBar?.visibility = View.GONE
            WebViewHookHelper.fitScreen(view, url)
            refreshLayout?.finishRefresh()
            showComplete()
        }
    }

    private inner class AppBrowserChromeClient constructor(view: BrowserView) : BrowserChromeClient(view) {

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            Timber.d("onConsoleMessage：===> " + consoleMessage?.message())
            return true
        }

        /**
         * 收到网页标题
         */
        override fun onReceivedTitle(view: WebView, title: String?) {
            title?.let { setTitle(title) }
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
            icon?.let { setRightIcon(BitmapDrawable(resources, icon)) }
        }

        /**
         * 收到加载进度变化
         */
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar?.progress = newProgress
        }
    }
}