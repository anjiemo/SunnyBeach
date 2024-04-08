package cn.cqautotest.sunnybeach.ui.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.widget.BrowserView
import cn.cqautotest.sunnybeach.widget.BrowserView.BrowserChromeClient
import cn.cqautotest.sunnybeach.widget.BrowserView.BrowserViewClient
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.flyjingfish.android_aop_core.annotations.CheckNetwork
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.Locale

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/10/24
 *    desc   : 浏览器 Fragment
 */
class BrowserFragment : AppFragment<AppActivity>(), StatusAction, OnRefreshListener {

    companion object {

        private const val INTENT_KEY_IN_URL: String = "url"

        @Log
        fun newInstance(url: String): BrowserFragment {
            val fragment = BrowserFragment()
            val bundle = Bundle()
            bundle.putString(INTENT_KEY_IN_URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val hintLayout: StatusLayout? by lazy { findViewById(R.id.hl_browser_hint) }
    private val refreshLayout: SmartRefreshLayout? by lazy { findViewById(R.id.sl_browser_refresh) }
    private val browserView: BrowserView? by lazy { findViewById(R.id.wv_browser_view) }

    override fun getLayoutId(): Int {
        return R.layout.browser_fragment
    }

    override fun initView() {
        // 设置 WebView 生命周期回调
        browserView?.setLifecycleOwner(this)
        // 设置网页刷新监听
        refreshLayout?.setOnRefreshListener(this)
    }

    override fun initData() {
        browserView?.apply {
            setBrowserViewClient(AppBrowserViewClient())
            setBrowserChromeClient(BrowserChromeClient(this))
            loadUrl(getString(INTENT_KEY_IN_URL)!!)
        }
        showLoading()
    }

    override fun getStatusLayout(): StatusLayout {
        return hintLayout!!
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

    private inner class AppBrowserViewClient : BrowserViewClient() {
        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post {
                showError { reload() }
            }
        }

        /**
         * 开始加载网页
         */
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {}

        /**
         * 完成加载网页
         */
        override fun onPageFinished(view: WebView, url: String) {
            refreshLayout?.finishRefresh()
            showComplete()
        }

        /**
         * 跳转到其他链接
         */
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val scheme: String = Uri.parse(url).scheme ?: return true
            when (scheme.lowercase(Locale.getDefault())) {
                "http", "https" -> BrowserActivity.start(getAttachActivity()!!, url)
            }
            // 已经处理该链接请求
            return true
        }
    }
}