package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ArticleDetailActivityBinding
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.widget.BrowserView
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/24
 * desc   : 文章详情界面
 */
class ArticleDetailActivity : AppActivity(), StatusAction, OnRefreshListener {

    private lateinit var mBinding: ArticleDetailActivityBinding
    private lateinit var mStatusLayout: StatusLayout
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mRefreshLayout: SmartRefreshLayout
    private lateinit var mBrowserView: BrowserView

    override fun getLayoutId(): Int = R.layout.article_detail_activity

    override fun onBindingView() {
        mBinding = ArticleDetailActivityBinding.bind(viewBindingRoot)
    }

    override fun initView() {
        mStatusLayout = mBinding.hlArticleDetailHint
        mProgressBar = mBinding.pbBrowserProgress
        mRefreshLayout = mBinding.slBrowserRefresh
        mBrowserView = mBinding.wvBrowserView

        // 设置 WebView 生命管控
        mBrowserView.setLifecycleOwner(this)
        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this)
    }

    override fun initData() {
        showLoading()
        mBrowserView.setBrowserViewClient(MyBrowserViewClient())
        mBrowserView.setBrowserChromeClient(MyBrowserChromeClient(mBrowserView))
        mBrowserView.loadUrl(getString(IntentKey.URL))
    }

    override fun getStatusLayout(): StatusLayout {
        return mStatusLayout
    }

    override fun onLeftClick(view: View?) {
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBrowserView.canGoBack()) {
            // 后退网页并且拦截该事件
            mBrowserView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 重新加载当前页
     */
    @CheckNet
    private fun reload() {
        mBrowserView.reload()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        reload()
    }

    inner class MyBrowserViewClient : BrowserView.BrowserViewClient() {
        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post { showError { reload() } }
        }

        /**
         * 开始加载网页
         */
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            mProgressBar.visibility = View.VISIBLE
        }

        /**
         * 完成加载网页
         */
        override fun onPageFinished(view: WebView, url: String) {
            mProgressBar.visibility = View.GONE
            mRefreshLayout.finishRefresh()
            showComplete()
        }
    }

    inner class MyBrowserChromeClient(view: BrowserView?) :
        BrowserView.BrowserChromeClient(view) {
        /**
         * 收到网页标题
         */
        override fun onReceivedTitle(view: WebView, title: String?) {
            if (title != null) {
                setTitle(title)
            }
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
            if (icon != null) {
                rightIcon = BitmapDrawable(resources, icon)
            }
        }

        /**
         * 收到加载进度变化
         */
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            mProgressBar.progress = newProgress
        }
    }

    companion object {

        @JvmStatic
        @CheckNet
        @DebugLog
        fun start(context: Context, url: String?) {
            if (TextUtils.isEmpty(url)) {
                return
            }
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra(IntentKey.URL, url)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}