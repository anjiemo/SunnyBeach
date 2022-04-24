package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import android.os.Build
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PlayerActivityBinding
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.util.fromJson
import cn.cqautotest.sunnybeach.util.startActivity
import cn.cqautotest.sunnybeach.util.toJson
import cn.cqautotest.sunnybeach.viewmodel.CourseViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.dylanc.longan.intentExtras

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/24
 * desc   : 课程视频播放界面，
 * 阿里云在线视频播放 SDK 文档：https://help.aliyun.com/document_detail/125553.html
 * 阿里云在线视频播放网页在线配置地址：https://player.alicdn.com/aliplayer/setting/setting.html
 */
class PlayerActivity : AppActivity(), StatusAction {

    private val mBinding by viewBinding<PlayerActivityBinding>()
    private val mCourseViewModel by viewModels<CourseViewModel>()
    private val courseChapterItemChildJson by intentExtras<String>(COURSE_CHAPTER_ITEM_CHILD)
    private val item by lazy { fromJson<CourseChapter.CourseChapterItem.Children>(courseChapterItemChildJson) }

    override fun getLayoutId(): Int = R.layout.player_activity

    override fun initView() {

    }

    override fun initData() {
        val webView = mBinding.wvBrowserView
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
        mCourseViewModel.getCoursePlayAuth(item.id).observe(this) {
            val coursePlayAuth = it.getOrElse {
                toast("播放凭证获取失败")
                return@observe
            }
            val videoId = coursePlayAuth.videoId
            val playAuth = coursePlayAuth.playAuth
            webView.loadUrl("file:///android_asset/player/player.html?videoId=${videoId}&playAuth=${playAuth}")
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlBrowserHint

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