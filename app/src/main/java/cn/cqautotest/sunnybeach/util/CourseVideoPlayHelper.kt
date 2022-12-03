package cn.cqautotest.sunnybeach.util

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.ktx.ifLoginThen
import cn.cqautotest.sunnybeach.ktx.tryShowLoginDialog
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.vodplayerview.activity.AliyunPlayerSkinActivity
import com.dylanc.longan.toast
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/24
 * desc   : 课程视频播放帮助类
 * 阿里云视频播放 SDK 文档：
 * 运行Demo源码：https://help.aliyun.com/document_detail/383401.html?spm=a2c4g.11186623.0.0.7bd24addVQH3VE
 * 快速集成：https://help.aliyun.com/document_detail/124711.html?spm=a2c4g.11186623.0.0.7bd24addVQH3VE
 */
object CourseVideoPlayHelper {

    fun play(activity: AppCompatActivity, courseViewModel: CourseViewModel, videoId: String) {
        activity.ifLoginThen { activity.getCoursePlayAuth(courseViewModel, videoId) }
    }

    private fun AppCompatActivity.getCoursePlayAuth(courseViewModel: CourseViewModel, _videoId: String) {
        courseViewModel.getCoursePlayAuth(_videoId).observe(this) { result ->
            result.onSuccess {
                val videoId = it.videoId
                val playAuth = it.playAuth
                Timber.d("getCoursePlayAuth：===> videoId is $videoId playAuth is $playAuth")
                GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH
                GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = true
                GlobalPlayerConfig.mVid = videoId
                GlobalPlayerConfig.mPlayAuth = playAuth
                startPlay()
            }.onFailure {
                when (it) {
                    is NotLoginException -> tryShowLoginDialog()
                    else -> toast("播放凭证获取失败")
                }
            }
        }
    }

    /**
     * 开启播放界面
     */
    fun Activity.startPlay() {
        val intent = Intent(this, AliyunPlayerSkinActivity::class.java)
        startActivityForResult(intent, 0)
    }
}