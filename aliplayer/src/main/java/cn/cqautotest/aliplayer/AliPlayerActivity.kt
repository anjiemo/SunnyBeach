package cn.cqautotest.aliplayer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.vodplayerview.activity.AliyunPlayerSkinActivity

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/02
 * desc   : 阿里云视频播放
 */
class AliPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ali_player_activity)
        initView()
        initData()
        initEvent()
    }

    private fun initView() {

    }

    private fun initData() {
        GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH
        intent.apply {
            GlobalPlayerConfig.mVid = getStringExtra(VIDEO_ID)
            GlobalPlayerConfig.mPlayAuth = getStringExtra(PLAY_AUTH)
            GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = true
        }
        startPlay()
    }

    /**
     * 开启播放界面
     */
    private fun startPlay() {
        val intent = Intent(this, AliyunPlayerSkinActivity::class.java)
        startActivity(intent)
    }

    private fun initEvent() {

    }

    companion object {

        private const val TAG = "AliPlayerActivity"
        private const val VIDEO_ID = "video_id"
        private const val PLAY_AUTH = "play_auth"

        fun start(context: Context, videoId: String, playAuth: String) {
            Intent(context, AliPlayerActivity::class.java).apply {
                takeUnless { context is Activity }?.let { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                putExtra(VIDEO_ID, videoId)
                putExtra(PLAY_AUTH, playAuth)
                context.startActivity(this)
            }
        }
    }
}