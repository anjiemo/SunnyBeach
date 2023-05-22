package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.SurfaceHolder
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PlayerActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.course.CoursePlayAuth
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.bean.InfoCode
import com.aliyun.player.source.VidAuth
import com.dylanc.longan.intentExtras
import com.dylanc.longan.windowInsetsControllerCompat
import com.gyf.immersionbar.ImmersionBar
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/24
 * desc   : 课程视频播放界面
 * 阿里云视频播放 SDK 文档：
 * 运行Demo源码：https://help.aliyun.com/document_detail/383401.html?spm=a2c4g.11186623.0.0.7bd24addVQH3VE
 * 快速集成：https://help.aliyun.com/document_detail/124711.html?spm=a2c4g.11186623.0.0.7bd24addVQH3VE
 */
class PlayerActivity : AppActivity() {

    private val mCourseViewModel by viewModels<CourseViewModel>()
    private val mBinding by viewBinding(PlayerActivityBinding::bind)
    private val mCoursePlayAuthJson by intentExtras<String>(COURSE_PLAY_AUTH)
    private val mCoursePlayAuth by lazy { fromJson<CoursePlayAuth>(mCoursePlayAuthJson) }
    private val mVideoTitle by intentExtras(VIDEO_TITLE, "")
    private val mScreenOrientation by intentExtras(SCREEN_ORIENTATION, DEFAULT_SCREEN_ORIENTATION)
    private lateinit var mAliPlayer: AliPlayer
    private val mHideTopBarControllerTask = Runnable {
        hideTopBarController()
    }
    private val mHideMediaControllerTask = Runnable {
        hideMediaController()
    }

    // 是否在后台
    private var mIsOnBackground = false

    // 是否暂停
    private var mIsPause = false

    // 刘海屏的高度
    private var mNotchHeight = 0

    override fun getLayoutId(): Int = R.layout.player_activity

    override fun initView() {
        // 隐藏状态栏
        windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.statusBars())
        // 隐藏底部导航栏
        windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.navigationBars())
        // 延迟获取刘海屏的高度
        window.decorView.post { mNotchHeight = ImmersionBar.getNotchHeight(this) }
        // 设置初始的屏幕方向
        requestedOrientation = mScreenOrientation
        mAliPlayer = createPlayer()
        mBinding.tvTitle.text = mVideoTitle
    }

    private fun createPlayer() = AliPlayerFactory.createAliPlayer(this)

    override fun initData() {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        mAliPlayer.isAutoPlay = true
        mAliPlayer.initAliPlayerListener()
        mBinding.apply {
            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {

                override fun surfaceCreated(holder: SurfaceHolder) {
                    mAliPlayer.setSurface(holder.surface)
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    mAliPlayer.surfaceChanged()
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mAliPlayer.setSurface(null)
                }
            })
            ivBack.setFixOnClickListener {
                finish()
            }
            flVideoContainer.setFixOnClickListener {
                toggleTopBarController()
                toggleMediaController()
                autoHideTopBarController()
                autoHideMediaController()
            }
            ivPlay.setFixOnClickListener {
                onPauseClick()
                autoHideTopBarController()
                autoHideMediaController()
            }
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    mAliPlayer.seekTo(seekBar.progress.toLong(), IPlayer.SeekMode.Inaccurate)
                    autoHideTopBarController()
                    autoHideMediaController()
                }
            })
            ivFullScreen.setFixOnClickListener {
                toggleScreenOrientation()
                autoHideTopBarController()
                autoHideMediaController()
            }
        }
        startPlay()
    }

    private fun toggleScreenOrientation() {
        requestedOrientation = when (requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun AliPlayer.initAliPlayerListener() {
        setOnErrorListener {
            // 错误码
            val errorCode = it.code
            // 错误描述
            val errorMsg = it.msg
            // 出错后需要停止播放器
            stop()
        }
        setOnPreparedListener {
            // 调用aliPlayer.prepare()方法后，播放器开始读取并解析数据。成功后，会回调此接口。
            mBinding.apply {
                seekBar.max = mAliPlayer.duration.toInt()
                seekBar.progress = 0
            }
            // 一般调用start开始播放视频。
            takeUnless { mIsPause || mIsOnBackground }?.let {
                start()
                updatePlayBtnState()
                autoHideTopBarController()
                autoHideMediaController()
            }
        }
        setOnCompletionListener {
            // 播放完成之后，就会回调到此接口。
            // 一般调用stop停止播放视频。
            stop()
        }
        setOnInfoListener {
            // 播放器中的一些信息，包括：当前进度、缓存位置等等。
            // 信息码
            val code = it.code
            // 信息内容
            val msg = it.extraMsg
            // 信息值
            val value = it.extraValue

            // 当前进度：InfoCode.CurrentPosition
            // 当前缓存位置：InfoCode.BufferedPosition
            Timber.d("initEvent：===> value is $value")
            when (it.code) {
                InfoCode.CurrentPosition -> {
                    // extraValue为当前播放进度，单位为毫秒
                    val extraValue = value.toInt()
                    mBinding.seekBar.progress = extraValue
                }
                else -> {}
            }
        }
        setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {

            override fun onLoadingBegin() {
                // 开始加载。画面和声音不足以播放。
                // 一般在此处显示圆形加载。
                showLoading()
            }

            override fun onLoadingProgress(percent: Int, netSpeed: Float) {
                // 加载进度。百分比和网速。
            }

            override fun onLoadingEnd() {
                // 结束加载。画面和声音可以播放。
                // 一般在此处隐藏圆形加载。
                hideLoading()
            }
        })
    }

    private fun showLoading() {
        mBinding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        mBinding.progressBar.isVisible = false
    }

    /**
     * 开启播放界面
     */
    private fun startPlay() {
        val videoId = mCoursePlayAuth.videoId
        val playAuth = mCoursePlayAuth.playAuth
        Timber.d("startPlay：===> videoId is $videoId playAuth is $playAuth")
        mAliPlayer.setDataSource(VidAuth().apply {
            vid = videoId
            setPlayAuth(playAuth)
            region = "cn-shanghai"
        })
        mAliPlayer.prepare()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                // 竖屏
                mBinding.llTopBarController.updatePadding(top = mNotchHeight)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                // 横屏
                mBinding.llTopBarController.updatePadding(top = 0)
            }
            else -> {}
        }
    }

    private fun onPauseClick() {
        if (mIsPause) {
            resumePlay()
        } else {
            pausePlay()
        }
    }

    /**
     * 暂停播放
     */
    private fun pausePlay() {
        mIsPause = true
        updatePlayBtnState()
        mAliPlayer.pause()
    }

    /**
     * 恢复播放
     */
    private fun resumePlay() {
        mIsPause = false
        updatePlayBtnState()
        mAliPlayer.start()
    }

    private fun updatePlayBtnState() {
        mBinding.ivPlay.isSelected = !mIsPause
    }

    private fun topBarControllerIsVisible() = mBinding.llTopBarController.isVisible

    private fun mediaControllerIsVisible() = mBinding.llMediaController.isVisible

    private fun toggleTopBarController() {
        if (topBarControllerIsVisible()) hideTopBarController() else showTopBarController()
    }

    private fun toggleMediaController() {
        if (mediaControllerIsVisible()) hideMediaController() else showMediaController()
    }

    private fun hideTopBarController() {
        mBinding.llTopBarController.isVisible = false
    }

    private fun hideMediaController() {
        mBinding.llMediaController.isVisible = false
    }

    private fun showTopBarController() {
        mBinding.llTopBarController.isVisible = true
    }

    private fun showMediaController() {
        mBinding.llMediaController.isVisible = true
    }

    private fun autoHideTopBarController() {
        removeCallbacks(mHideTopBarControllerTask)
        postDelayed(mHideTopBarControllerTask, TimeUnit.SECONDS.toMillis(3))
    }

    private fun autoHideMediaController() {
        removeCallbacks(mHideMediaControllerTask)
        postDelayed(mHideMediaControllerTask, TimeUnit.SECONDS.toMillis(3))
    }

    override fun onPause() {
        super.onPause()
        mIsOnBackground = true
        pausePlay()
    }

    override fun onResume() {
        super.onResume()
        mIsOnBackground = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mAliPlayer.stop()
        mAliPlayer.release()
        mAliPlayer.setSurface(null)
    }

    companion object {

        private const val COURSE_PLAY_AUTH = "COURSE_PLAY_AUTH"
        private const val SCREEN_ORIENTATION = "SCREEN_ORIENTATION"
        private const val VIDEO_TITLE = "VIDEO_TITLE"

        // 默认屏幕方向
        private const val DEFAULT_SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        @Log
        fun start(
            context: Context,
            coursePlayAuth: CoursePlayAuth,
            videoTitle: String = "",
            screenOrientation: Int = DEFAULT_SCREEN_ORIENTATION
        ) {
            context.startActivity<PlayerActivity> {
                putExtra(COURSE_PLAY_AUTH, coursePlayAuth.toJson())
                putExtra(VIDEO_TITLE, videoTitle)
                putExtra(SCREEN_ORIENTATION, screenOrientation)
            }
        }
    }
}