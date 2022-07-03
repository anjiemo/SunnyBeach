package cn.cqautotest.aliplayer

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.source.VidAuth

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/02
 * desc   : 阿里云视频播放
 */
class AliPlayerActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    private lateinit var mTextureView: TextureView
    private lateinit var mAliPlayer: AliPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ali_player_activity)
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        mTextureView = findViewById(R.id.texture_view)
    }

    private fun initData() {
        // 创建播放器
        mAliPlayer = AliPlayerFactory.createAliPlayer(this)
        // 视频 id
        val videoId = ""
        // 播放凭证
        val playAuth = ""
        // 点播服务的接入地域，默认为cn-shanghai
        val region = "cn-shanghai"
        VidAuth().apply {
            vid = videoId
            setPlayAuth(playAuth)
            setRegion(region)
        }.also { mAliPlayer.setDataSource(it) }
        mTextureView.surfaceTextureListener = this
    }

    private fun initEvent() {
        with(mAliPlayer) {
            // 此回调会在使用播放器的过程中，出现了任何错误，都会回调此接口。
            setOnErrorListener {
                // 错误码
                val errorCode = it.code
                // 错误描述
                val errorMsg = it.msg
                Log.d(TAG, "onCreate: ===> errorCode is $errorCode errorMsg is $errorMsg")
                // 出错后需要停止掉播放器
                stop()
            }
            // 调用 player.prepare() 方法后，播放器开始读取并解析数据。成功后会回调此接口。
            setOnPreparedListener { start() }
            // 播放完成之后，就会回调到此接口。一般调用stop停止播放视频
            setOnCompletionListener { stop() }
            // 播放器中的一些信息，包括：当前进度、缓存位置等等
            setOnInfoListener {
                // 信息码
                val code = it.code
                // 信息内容
                val msg = it.extraMsg
                // 信息值
                val value = it.extraValue
                // 当前进度：InfoCode.CurrentPosition
                // 当前缓存位置：InfoCode.BufferedPosition
            }
            // 播放器的加载状态，网络不佳时，用于展示加载画面。
            setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
                override fun onLoadingBegin() {
                    // 开始加载。画面和声音不足以播放。
                    // 一般在此处显示圆形加载
                }

                override fun onLoadingProgress(percent: Int, netSpeed: Float) {
                    // 加载进度。百分比和网速。
                }

                override fun onLoadingEnd() {
                    // 结束加载。画面和声音可以播放。
                    // 一般在此处隐藏圆形加载。
                }
            })
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mAliPlayer.setSurface(Surface(surface))
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        mAliPlayer.surfaceChanged()
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mAliPlayer.setSurface(null)
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    companion object {
        private const val TAG = "AliPlayerActivity"
    }
}