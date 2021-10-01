package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ArticleDetailActivityBinding
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.util.logByDebug
import cn.cqautotest.sunnybeach.widget.BrowserView
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/24
 * desc   : 文章详情界面
 */
class ArticleDetailActivity : AppActivity(), StatusAction, OnRefreshListener {

    private lateinit var mBinding: ArticleDetailActivityBinding
    private var mArticleId = ""
    private var mArticleTitle = ""

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = ArticleDetailActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initObserver() {}

    override fun initEvent() {}

    override fun initData() {
        showLoading()
        mArticleId = intent.getStringExtra(IntentKey.ID) ?: ""
        mArticleTitle = intent.getStringExtra(IntentKey.TITLE) ?: ""
        mBinding.titleBar.title = mArticleTitle
        loadArticleDetail()
    }

    override fun initView() {
        val webView = mBinding.wvBrowserView
        // 设置 WebView 生命管控
        webView.setLifecycleOwner(this)
        webView.settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            textZoom = 125
        }
        webView.setInitialScale(144)
    }

    /**
     * 根据文章id加载文章详情
     */
    private fun loadArticleDetail() {
        val webView = mBinding.wvBrowserView
        webView.loadUrl("https://www.sunofbeach.net/a/${mArticleId}")
        webView.setBrowserViewClient(MyBrowserViewClient(this))
    }

    override fun getStatusLayout(): StatusLayout {
        return mBinding.hlArticleDetailHint
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadArticleDetail()
    }

    override fun isStatusBarDarkFont(): Boolean = false

    companion object {

        private class FitScreen(webView: WebView) :
            Runnable {

            private val mWebView = WeakReference(webView)

            override fun run() {
                val webView = mWebView.get() ?: return
                webView.evaluateJavascript(
                    "var child=document.getElementById(\"header-container\");\n" +
                            "child.parentNode.removeChild(child);", null
                )
                webView.evaluateJavascript(
                    "var child=document.getElementById(\"article-detail-left-part\");\n" +
                            "child.parentNode.removeChild(child);", null
                )
                webView.evaluateJavascript(
                    "var child=document.getElementById(\"article-detail-right-part\");\n" +
                            "child.parentNode.removeChild(child);", null
                )
                webView.evaluateJavascript(
                    "var child=document.getElementById(\"footer-container\");\n" +
                            "child.parentNode.removeChild(child);", null
                )
                webView.evaluateJavascript(
                    "var child=document.getElementById('article-detail-center-part');\n" +
                            "child.style.margin='-20px 0 0 0';", null
                )
                webView.evaluateJavascript(
                    "var child=document.getElementById('main-content');\n" +
                            "child.style.width='750px';", null
                )
            }
        }

        private class MyBrowserViewClient(activity: ArticleDetailActivity) :
            BrowserView.BrowserViewClient() {

            private val mActivity: WeakReference<ArticleDetailActivity> = WeakReference(activity)

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                logByDebug(msg = "onPageFinished：===> pageFinished")
                view ?: return
                // 在网页加载完毕后才调用，否则可能没有渲染出要操作的 dom 元素
                mActivity.get()?.let {
                    it.lifecycleScope.launch {
                        FitScreen(view).run()
                        if (view.progress == 100) {
                            // 网页加载完毕时再显示界面，否则会有页面闪烁的效果
                            it.showComplete()
                        }
                    }
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                mActivity.get()?.showError {
                    mActivity.get()?.loadArticleDetail()
                }
            }
        }

        /**
         * 文章id、文章标题
         */
        @JvmStatic
        @CheckNet
        @DebugLog
        fun start(context: Context, articleId: String?, articleTitle: String?) {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.run {
                putExtra(IntentKey.ID, articleId)
                putExtra(IntentKey.TITLE, articleTitle)
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        }
    }
}