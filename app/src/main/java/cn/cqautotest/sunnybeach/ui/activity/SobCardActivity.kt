package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SobCardActivityBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.toQrCodeBitmapOrNull
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_VIEW_USER_URL_PRE
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.IntentUtils
import com.bumptech.glide.Glide
import com.dylanc.longan.intentExtras
import com.hjq.bar.TitleBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Pattern

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/07/07
 * desc   : 阳光沙滩证查看界面
 */
class SobCardActivity : AppActivity() {

    private val mBinding by viewBinding<SobCardActivityBinding>()
    private val mSobId by intentExtras(SOB_ID, "")
    private var mShareSobCardJob: Job? = null

    override fun getLayoutId() = R.layout.sob_card_activity

    override fun initView() {
        with(mBinding) {
            tvSobId.text = mSobId.manicured()
            Glide.with(context)
                .load("$SUNNY_BEACH_VIEW_USER_URL_PRE${mSobId}".toQrCodeBitmapOrNull())
                .into(ivSobQrCode)
        }
    }

    private fun String.manicured(): String {
        val matcher = pattern.matcher(this)
        return matcher.replaceAll("$1\u3000$2\u3000$3\u3000$4\u3000$5\u3000$6")
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun onRightClick(titleBar: TitleBar) {
        mShareSobCardJob?.cancel()
        mShareSobCardJob = lifecycleScope.launch {
            val sobCardFile = withContext(Dispatchers.IO) {
                // 默认有黑色的背景
                val bitmap = ConvertUtils.view2Bitmap(mBinding.flSobCard)
                val bitmapBytes = ConvertUtils.bitmap2Bytes(bitmap)
                File.createTempFile("sob_", null).also { it.writeBytes(bitmapBytes) }
            }
            val intent = IntentUtils.getShareImageIntent(sobCardFile)
            startActivity(intent)
        }
    }

    override fun isStatusBarDarkFont() = true

    companion object {

        private const val SOB_ID = "sob_id"

        // 分隔规则
        private const val REGEX = "(\\w{4})(\\w{3})(\\w{3})(\\w{3})(\\w{3})(\\w{3})"
        private val pattern = Pattern.compile(REGEX)

        fun start(context: Context, sobId: String) {
            context.startActivity<SobCardActivity> {
                putExtra(SOB_ID, sobId)
            }
        }
    }
}