package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ScanResultActivityBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import com.blankj.utilcode.util.ClipboardUtils
import com.dylanc.longan.intentExtras
import com.dylanc.longan.startActivity
import com.huawei.hms.ml.scan.HmsScan
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/07/25
 * desc   : 扫码结果处理界面
 */
class ScanResultActivity : AppActivity() {

    private val mBinding by viewBinding(ScanResultActivityBinding::bind)
    private val mHmsScan by intentExtras<HmsScan?>(HMS_SCAN_RESULT)

    override fun getLayoutId(): Int = R.layout.scan_result_activity

    override fun initView() {

    }

    override fun initData() {
        mHmsScan?.let {
            showResult(it)
        } ?: showUnknownType()
    }

    private fun showResult(hmsScan: HmsScan) {
        val scanTypeForm = hmsScan.getScanTypeForm()
        val originalValue = hmsScan.getOriginalValue()
        Timber.d("showResult：===> scanTypeForm is $scanTypeForm originalValue is $originalValue")
        mBinding.tvContent.text = originalValue
        when (scanTypeForm) {
            // 产品信息条码类型
            HmsScan.ARTICLE_NUMBER_FORM -> {
                showUnknownType()
            }
            // 联系人信息条码类型
            HmsScan.CONTACT_DETAIL_FORM -> {
                showUnknownType()
            }
            // 驾照信息条码类型
            HmsScan.DRIVER_INFO_FORM -> {
                showUnknownType()
            }
            // 邮箱条码类型
            HmsScan.EMAIL_CONTENT_FORM -> {
                showUnknownType()
            }
            // 日历事件条码类型
            HmsScan.EVENT_INFO_FORM -> {
                showUnknownType()
            }
            // ISBN号条码类型
            HmsScan.ISBN_NUMBER_FORM -> {
                showUnknownType()
            }
            // 定位信息条码类型
            HmsScan.LOCATION_COORDINATE_FORM -> {
                showUnknownType()
            }
            // 文本条码类型
            HmsScan.PURE_TEXT_FORM -> {
                showTextContentType()
            }
            // 短信条码类型
            HmsScan.SMS_FORM -> {
                showUnknownType()
            }
            // 电话号码条码类型
            HmsScan.TEL_PHONE_NUMBER_FORM -> {
                showUnknownType()
            }
            // URL链接二维码类型
            HmsScan.URL_FORM -> {
                showUrlContentType()
            }
            // Wi-Fi二维码类型
            HmsScan.WIFI_CONNECT_INFO_FORM -> {
                showUnknownType()
            }
            // 未知的码类型
            else -> {
                showUnknownType()
            }
        }
    }

    private fun showUrlContentType() {
        mBinding.apply {
            tvAccessLink.isVisible = true
            tvType.text = "内容类型：链接"
        }
    }

    private fun showTextContentType() {
        mBinding.apply {
            tvAccessLink.isVisible = false
            tvType.text = "内容类型：文本"
        }
    }

    private fun showUnknownType() {
        mBinding.apply {
            tvAccessLink.isVisible = false
            tvType.text = "内容类型：未知"
        }
    }

    override fun initEvent() {
        mBinding.apply {
            tvAccessLink.setFixOnClickListener {
                mHmsScan?.getShowResult()?.let { BrowserActivity.start(context, it) }
            }
            tvCopy.setFixOnClickListener {
                ClipboardUtils.copyText(tvContent.text)
                toast(R.string.common_copy_success)
            }
        }
    }

    companion object {

        private const val HMS_SCAN_RESULT = "HMS_SCAN_RESULT"

        @JvmStatic
        fun start(context: Context, hmsScan: HmsScan?) {
            context.startActivity<ScanResultActivity>(HMS_SCAN_RESULT to hmsScan)
        }
    }
}