package cn.cqautotest.sunnybeach.ui.activity

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.KeyEvent
import android.webkit.WebView
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PlayerActivityBinding
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import cn.cqautotest.sunnybeach.widget.BrowserView
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.blankj.utilcode.util.ScreenUtils
import com.dylanc.longan.intentExtras
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import okhttp3.FormBody

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/24
 * desc   : 课程视频播放界面，
 * 阿里云在线视频播放 SDK 文档：https://help.aliyun.com/document_detail/125553.html
 * 阿里云在线视频播放网页在线配置地址：https://player.alicdn.com/aliplayer/setting/setting.html
 */
class PlayerActivity : AppActivity(), StatusAction, OnRefreshListener {

    private val mBinding by viewBinding<PlayerActivityBinding>()
    private val mCourseViewModel by viewModels<CourseViewModel>()
    private val courseChapterItemChildJson by intentExtras<String>(COURSE_CHAPTER_ITEM_CHILD)
    private val item by lazy { fromJson<CourseChapter.CourseChapterItem.Children>(courseChapterItemChildJson) }

    override fun getLayoutId(): Int = R.layout.player_activity

    override fun initView() {
        val webView = mBinding.wvBrowserView
        webView.apply {
            setBrowserViewClient(AppBrowserViewClient())
            setBrowserChromeClient(BrowserView.BrowserChromeClient(this))
            // 设置 WebView 生命管控
            webView.setLifecycleOwner(this@PlayerActivity)
        }
        // 设置网页刷新监听
        mBinding.slBrowserRefresh.setOnRefreshListener(this)

        if (item == null) {
            showError { initData() }
            return
        }
        // 设置可以跨域请求
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val settings = webView.settings
                val clazz = settings::class.java
                val method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", Boolean::class.java)
                method.invoke(settings, true)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun initData() {
        showLoading()
        checkToken {
            it.getOrElse {
                showLoginDialog()
                showError { initData() }
                return@checkToken
            }
            getCoursePlayAuth()
        }
    }

    private fun getCoursePlayAuth() {
        mCourseViewModel.getCoursePlayAuth(item.id).observe(this) { result ->
            val coursePlayAuth = result.getOrElse { t ->
                when (t) {
                    is NotLoginException -> showError { initData() }
                    else -> toast("播放凭证获取失败")
                }
                return@observe
            }
            val videoId = coursePlayAuth.videoId
            val playAuth = coursePlayAuth.playAuth
            val screenWidth = ScreenUtils.getAppScreenWidth()
            val screenHeight = ScreenUtils.getAppScreenHeight()
            val queryParams = FormBody.Builder()
                .add("screenWidth", screenWidth.toString())
                .add("screenHeight", screenHeight.toString())
                .add("videoId", videoId)
                .add("playAuth", playAuth)
                .build().toQueryParams()
            mBinding.wvBrowserView.loadUrl("${ASSET_PREFIX}player/player.html?${queryParams}")
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        mBinding.wvBrowserView.apply {
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
        mBinding.wvBrowserView.reload()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        reload()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlBrowserHint

    private inner class AppBrowserViewClient : BrowserView.BrowserViewClient() {

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
            showLoading()
        }

        /**
         * 完成加载网页
         */
        override fun onPageFinished(view: WebView, url: String) {
            showComplete()
        }
    }

    companion object {

        /**
         * [AssetUriLoader]
         */
        private const val ASSET_PATH_SEGMENT = "android_asset"
        private const val ASSET_PREFIX = ContentResolver.SCHEME_FILE + ":///" + ASSET_PATH_SEGMENT + "/"
        private const val COURSE_CHAPTER_ITEM_CHILD = "courseChapterItemChild"

        @Log
        fun start(context: Context, item: CourseChapter.CourseChapterItem.Children) {
            context.startActivity<PlayerActivity> {
                putExtra(COURSE_CHAPTER_ITEM_CHILD, item.toJson())
            }
        }
    }
}