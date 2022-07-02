package cn.cqautotest.sunnybeach.ui.activity

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.KeyEvent
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.CheckNet
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PlayerActivityBinding
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.ktx.*
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
import timber.log.Timber

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
            setBrowserChromeClient(AppBrowserChromeClient(this))
            // 设置 WebView 生命管控
            webView.setLifecycleOwner(this@PlayerActivity)
            addJavascriptInterface(this@PlayerActivity, "AndroidNative")
        }
        // 设置网页刷新监听
        mBinding.slBrowserRefresh.setOnRefreshListener(this)

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
        checkToken {
            it.onSuccess { getCoursePlayAuth() }.onFailure {
                showLoginDialog()
                showError { reload() }
            }
        }
    }

    private fun getCoursePlayAuth() {
        mCourseViewModel.getCoursePlayAuth(item.id).observe(this) { result ->
            result.onSuccess {
                val videoId = it.videoId
                val playAuth = it.playAuth
                Timber.d("getCoursePlayAuth：===> videoId is $videoId playAuth is $playAuth")
                val screenWidth = ScreenUtils.getAppScreenWidth()
                val screenHeight = ScreenUtils.getAppScreenHeight()
                val queryParams = FormBody.Builder()
                    .add("screenWidth", "${screenWidth}px")
                    .add("screenHeight", "${screenHeight}px")
                    .add("videoId", videoId)
                    .add("playAuth", playAuth)
                    .build().toQueryParams()
                mBinding.wvBrowserView.loadUrl("${ASSET_PREFIX}player/player.html?${queryParams}")
            }.onFailure {
                when (it) {
                    is NotLoginException -> showError { reload() }
                    else -> toast("播放凭证获取失败")
                }
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
        initData()
    }

    /**
     * [OnRefreshListener]
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        reload()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlBrowserHint

    @WorkerThread
    @JavascriptInterface
    fun onPlayerCreated() {
        Timber.d("onPlayerCreated：===> 播放器初始化完成")
    }

    @WorkerThread
    @JavascriptInterface
    fun onSnapshot() {
        Timber.d("onSnapshot：===> 触发截图操作")
    }

    /**
     * 播放器全屏，仅H5支持。
     * fullscreenService.requestFullScreen
     */
    @MainThread
    fun fullScreenVideo() {
        mBinding.wvBrowserView.evaluateJavascript("javascript:player.fullscreenService.requestFullScreen();", null)
    }

    /**
     * 播放器取消全屏事件，仅H5支持。
     * fullscreenService.requestFullScreen
     */
    @MainThread
    fun cancelFullScreen() {
        mBinding.wvBrowserView.evaluateJavascript("javascript:player.fullscreenService.cancelFullScreen();", null)
    }

    /**
     * 开始播放视频（注意：是从当前位置开始播放，不是从初始位置播放）
     * player.play()
     */
    fun startPlayVideo() {

    }

    /**
     * 暂停播放视频
     * player.pause()
     */
    fun pausePlayVideo() {

    }

    /**
     * 重播视频
     * player.replay()
     */
    fun replayVideo() {

    }

    /**
     * 从指定时间开始播放
     * time为指定的时间，单位：秒。
     * player.seek(time)
     */
    fun seekPayTime(time: Long) {

    }

    /**
     * 获取当前播放进度（接口返回的时间单位为秒）
     * player.getCurrentTime()
     */
    fun getCurrentTime() {

    }

    /**
     * 获取视频时长（指获取视频总时长。需要在视频加载完成以后才可以获取到，可以在play事件后获取。）
     * 调用网页视频播放器中的方法
     * player.getDuration()
     */
    fun getVideoDuration() {

    }

    /**
     * 获取播放状态
     * player.getStatus()
     *
     * init：初始化。
     * ready：准备。
     * loading：加载中。
     * play：播放。
     * pause：暂停。
     * playing：正在播放。
     * waiting：等待缓冲。
     * error：错误。
     * ended：结束。
     */
    fun getPlayerStatus() {

    }

    /**
     * 设置视频静音
     * player.mute()
     */
    fun setVideoMute() {

    }

    /**
     * 设置播放器大小
     * player.setPlayerSize(w, h)
     * eg：400px or 60%
     */
    fun setPlayerSize(width: String, height: String) {

    }

    /**
     * 设置视频倍速播放
     * player.setSpeed(speed)
     * 手动设置播放的倍速，支持 0.5~2 倍速播放，倍速播放仅H5模式支持。
     */
    fun setVideoPlaySpeed(speed: Float) {
        if (speed < 0.5f || speed > 2) throw RuntimeException("are you ok? We do not support speed values other than [0.5, 2].")
    }

    private inner class AppBrowserViewClient : BrowserView.BrowserViewClient() {

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

    private inner class AppBrowserChromeClient constructor(view: BrowserView) : BrowserView.BrowserChromeClient(view) {

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            consoleMessage?.let {
                Timber.d("onConsoleMessage：===> ${it.message()}")
            }
            return super.onConsoleMessage(consoleMessage)
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