package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.vodplayerview.activity.AliyunPlayerSkinActivity
import com.dylanc.longan.intentExtras
import timber.log.Timber

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
    private val courseChapterItemChildJson by intentExtras<String>(COURSE_CHAPTER_ITEM_CHILD)
    private val item by lazy { fromJson<CourseChapter.CourseChapterItem.Children>(courseChapterItemChildJson) }

    override fun getLayoutId(): Int = 0

    override fun initView() {

    }

    override fun initData() {
        GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH
        GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = true
        ifLoginThen { getCoursePlayAuth() }
    }

    private fun getCoursePlayAuth() {
        mCourseViewModel.getCoursePlayAuth(item.id).observe(this) { result ->
            result.onSuccess {
                val videoId = it.videoId
                val playAuth = it.playAuth
                Timber.d("getCoursePlayAuth：===> videoId is $videoId playAuth is $playAuth")
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
    private fun startPlay() {
        val intent = Intent(this, AliyunPlayerSkinActivity::class.java)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    companion object {

        private const val COURSE_CHAPTER_ITEM_CHILD = "courseChapterItemChild"

        @Log
        fun start(context: Context, item: CourseChapter.CourseChapterItem.Children) {
            context.startActivity<PlayerActivity> {
                putExtra(COURSE_CHAPTER_ITEM_CHILD, item.toJson())
            }
        }
    }
}