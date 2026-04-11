package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.core.view.isVisible
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ScanResultActivityBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.scan.ScanResult
import com.blankj.utilcode.util.ClipboardUtils
import com.dylanc.longan.intentExtras
import com.dylanc.longan.startActivity
import dev.androidbroadcast.vbpd.viewBinding
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/07/25
 * desc   : 扫码结果处理界面
 */
class ScanResultActivity : AppActivity() {

    private val mBinding by viewBinding(ScanResultActivityBinding::bind)
    private val mScanResult by intentExtras<ScanResult?>(SCAN_RESULT)

    override fun getLayoutId(): Int = R.layout.scan_result_activity

    override fun initView() {

    }

    override fun initData() {
        mScanResult?.let {
            showResult(it)
        } ?: showUnknownType()
    }

    private fun showResult(scanResult: ScanResult) {
        val scanTypeForm = scanResult.getScanTypeForm()
        val originalValue = scanResult.getOriginalValue()
        Timber.d("showResult：===> scanTypeForm is $scanTypeForm originalValue is $originalValue")
        mBinding.tvContent.text = originalValue
        when (scanTypeForm) {
            // 文本内容类型
            ScanResult.PURE_TEXT_FORM -> {
                showTextContentType()
            }
            // URL 链接内容类型
            ScanResult.URL_FORM -> {
                showUrlContentType()
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
                mScanResult?.getShowResult()?.let { BrowserActivity.start(context, it) }
            }
            tvCopy.setFixOnClickListener {
                ClipboardUtils.copyText(tvContent.text)
                toast(R.string.common_copy_success)
            }
        }
    }

    companion object {

        private const val SCAN_RESULT = "SCAN_RESULT"

        @JvmStatic
        fun start(context: Context, scanResult: ScanResult?) {
            context.startActivity<ScanResultActivity>(SCAN_RESULT to scanResult)
        }
    }
}