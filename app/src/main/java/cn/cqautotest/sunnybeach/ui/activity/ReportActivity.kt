package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.ReportType
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.dylanc.longan.intentExtras
import com.hjq.bar.TitleBar

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/25
 * desc   : 举报页，支持的举报类型：1、文章，2、问答，3、动态，4、分享，5、文章评论，6、问答评论，7、动态评论，8、用户
 */
class ReportActivity : AppActivity() {

    private val mUserViewModel by viewModels<UserViewModel>()
    private val reportTypeJson by intentExtras<String>(REPORT_TYPE)
    private val reportType by lazy { fromJson<ReportType>(reportTypeJson) }
    private val contentId by intentExtras(CONTENT_ID, "")

    override fun getLayoutId(): Int = R.layout.report_activity

    override fun initView() {

    }

    override fun initData() {
        takeIf { contentId.isEmpty() }?.let {
            simpleToast("内容 id 不能为空")
            return
        }
        // TODO: 根据类型和内容 id 获取被举报内容的具体信息

    }

    override fun onRightClick(titleBar: TitleBar) {
        // 提交举报表单
        mUserViewModel.report(reportType = reportType, contentId = contentId, url = "", why = "")
    }

    companion object {

        private const val REPORT_TYPE = "report_type"
        private const val CONTENT_ID = "content_id"

        fun start(context: Context, reportType: ReportType, contentId: String) {
            context.startActivity<ReportActivity> {
                putExtra(REPORT_TYPE, reportType.toJson())
                putExtra(CONTENT_ID, contentId)
            }
        }
    }
}