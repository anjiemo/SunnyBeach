package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ReportActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.ReportType
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.dylanc.longan.intentExtras

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/25
 * desc   : 举报页，支持的举报类型：1、文章，2、问答，3、动态，4、分享，5、文章评论，6、问答评论，7、动态评论，8、用户
 */
class ReportActivity : AppActivity() {

    private val mBinding by viewBinding<ReportActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()
    private val reportTypeJson by intentExtras<String>(REPORT_TYPE)
    private val reportType: ReportType by lazy { fromJson(reportTypeJson) }
    private val contentId by intentExtras(CONTENT_ID, "")
    private var mUrl = ""

    override fun getLayoutId(): Int = R.layout.report_activity

    override fun initView() {

    }

    override fun initData() {
        takeIf { contentId.isEmpty() }?.let {
            simpleToast("内容 id 不能为空")
            finish()
            return
        }
        mBinding.titleBar.title = "举报${reportType.title}"
        // 根据举报类型获取网页 url 前缀
        val urlPre = when (reportType) {
            ReportType.ARTICLE, ReportType.ARTICLE_COMMENT -> SUNNY_BEACH_ARTICLE_URL_PRE
            ReportType.QA, ReportType.QA_COMMENT -> SUNNY_BEACH_QA_URL_PRE
            ReportType.FISH, ReportType.FISH_COMMENT -> SUNNY_BEACH_FISH_URL_PRE
            ReportType.SHARE -> SUNNY_BEACH_SHARE_URL_PRE
            ReportType.USER -> SUNNY_BEACH_VIEW_USER_URL_PRE
        }
        mUrl = urlPre + contentId
    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        mBinding.etInputContent.doAfterTextChanged {
            val inputContentLength = it?.length ?: 0
            mBinding.tvDesc.text = "当前已输入 $inputContentLength 字"
            mBinding.tvSubmit.isEnabled = inputContentLength > 5
        }
        mBinding.tvSubmit.setFixOnClickListener { submitReportContent() }
    }

    private fun submitReportContent() {
        mBinding.tvSubmit.isEnabled = false
        showDialog()
        // 提交举报表单
        val inputContent = mBinding.etInputContent.textString
        mUserViewModel.report(reportType = reportType, contentId = contentId, url = mUrl, why = inputContent).observe(this) { result ->
            mBinding.tvSubmit.isEnabled = true
            hideDialog()
            takeIf { result.isSuccess }?.let { finish() }
            val tips = result.getOrNull() ?: "举报失败"
            simpleToast(tips)
        }
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