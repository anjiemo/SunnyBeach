package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PlayerActivityBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import com.dylanc.longan.intentExtras
import com.dylanc.longan.windowInsetsControllerCompat
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/24
 * desc   : 课程视频播放界面
 */
@AndroidEntryPoint
class PlayerActivity : AppActivity() {

    private val mCourseViewModel by viewModels<CourseViewModel>()
    private val mBinding by viewBinding(PlayerActivityBinding::bind)
    private val mVideoId by intentExtras(VIDEO_ID, "")
    private val mVideoTitle by intentExtras(VIDEO_TITLE, "")
    private val mScreenOrientation by intentExtras(SCREEN_ORIENTATION, DEFAULT_SCREEN_ORIENTATION)
    private lateinit var mExoPlayer: ExoPlayer
    private var mProgressUpdateJob: Job? = null

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

    // 首帧是否已经渲染
    private var mIsFirstFrameRendered = false

    override fun getLayoutId(): Int = R.layout.player_activity

    override fun initView() {
        preloadVideoInfo()
        // 隐藏状态栏
        mBinding.root.windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.statusBars())
        // 隐藏底部导航栏
        mBinding.root.windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.navigationBars())
        // 延迟获取刘海屏的高度
        window.decorView.post { mNotchHeight = ImmersionBar.getNotchHeight(this) }
        // 设置初始的屏幕方向
        requestedOrientation = mScreenOrientation
        mBinding.tvTitle.text = mVideoTitle
        mExoPlayer = createPlayer()
    }

    private fun createPlayer() = ExoPlayer.Builder(this).build().apply {
        playWhenReady = true
    }

    override fun initData() {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        mExoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> showLoading()
                    Player.STATE_READY -> {
                        mIsFirstFrameRendered = true
                        hideLoading()
                        mBinding.seekBar.max = mExoPlayer.duration.toInt()
                        updatePlayBtnState()
                        startProgressUpdate()
                    }

                    Player.STATE_ENDED -> {
                        stopProgressUpdate()
                        mExoPlayer.pause()
                        updatePlayBtnState()
                    }

                    Player.STATE_IDLE -> {
                        stopProgressUpdate()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    startProgressUpdate()
                } else {
                    stopProgressUpdate()
                }
                updatePlayBtnState()
            }

            override fun onPlayerError(error: PlaybackException) {
                hideLoadingForce()
                Timber.e("ExoPlayer Error: ${error.message}")
                toast("播放失败：${error.message}")
                stopProgressUpdate()
            }
        })

        mBinding.apply {
            mExoPlayer.setVideoSurfaceHolder(surfaceView.holder)

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
                    stopProgressUpdate()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    mExoPlayer.seekTo(seekBar.progress.toLong())
                    if (mExoPlayer.isPlaying) {
                        startProgressUpdate()
                    }
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
    }

    private fun startProgressUpdate() {
        mProgressUpdateJob?.cancel()
        mProgressUpdateJob = lifecycleScope.launch {
            while (isActive) {
                mBinding.seekBar.progress = mExoPlayer.currentPosition.toInt()
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        mProgressUpdateJob?.cancel()
        mProgressUpdateJob = null
    }

    private fun toggleScreenOrientation() {
        requestedOrientation = when (requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun showLoading() {
        mBinding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        if (mIsFirstFrameRendered) {
            mBinding.progressBar.isVisible = false
        }
    }

    /**
     * 强制隐藏加载
     */
    private fun hideLoadingForce() {
        mBinding.progressBar.isVisible = false
    }

    /**
     * 预加载视频播放逻辑：
     * 1、触发 ViewModel 获取视频播放信息。
     * 2、观察 videoPlayState 状态流，驱动播放器状态变更。
     */
    private fun preloadVideoInfo() {
        val videoId = mVideoId
        // 预加载视频信息
        mCourseViewModel.fetchVideoInfo(videoId)
        lifecycleScope.launch {
            mCourseViewModel.videoPlayState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { state ->
                    when (state) {
                        is CourseViewModel.VideoPlayState.Idle -> {}
                        is CourseViewModel.VideoPlayState.Loading -> showLoading()
                        is CourseViewModel.VideoPlayState.Success -> {
                            val info = state.info
                            // 优先取第一个播放链接
                            val url = info.playUrls.firstOrNull()?.url
                            if (url != null) {
                                val mediaItem = MediaItem.fromUri(url)
                                mExoPlayer.setMediaItem(mediaItem)
                                mExoPlayer.prepare()
                            } else {
                                hideLoadingForce()
                                toast("未找到可播放的视频链接")
                                finish()
                            }
                        }

                        is CourseViewModel.VideoPlayState.Error -> {
                            val e = state.throwable
                            hideLoadingForce()
                            toast("视频加载失败：${e.message}")
                            Timber.e(e)
                        }
                    }
                }
        }
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
        mExoPlayer.pause()
    }

    /**
     * 恢复播放
     */
    private fun resumePlay() {
        mIsPause = false
        updatePlayBtnState()
        mExoPlayer.play()
    }

    private fun updatePlayBtnState() {
        mBinding.ivPlay.isSelected = mExoPlayer.isPlaying
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
        stopProgressUpdate()
        mCourseViewModel.resetVideoPlayState()
        mExoPlayer.release()
    }

    companion object {

        private const val VIDEO_ID = "VIDEO_ID"
        private const val SCREEN_ORIENTATION = "SCREEN_ORIENTATION"
        private const val VIDEO_TITLE = "VIDEO_TITLE"

        // 默认屏幕方向
        private const val DEFAULT_SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        @Log
        fun start(
            context: Context,
            videoId: String,
            videoTitle: String = "",
            screenOrientation: Int = DEFAULT_SCREEN_ORIENTATION
        ) {
            context.startActivity<PlayerActivity> {
                putExtra(VIDEO_ID, videoId)
                putExtra(VIDEO_TITLE, videoTitle)
                putExtra(SCREEN_ORIENTATION, screenOrientation)
            }
        }
    }
}