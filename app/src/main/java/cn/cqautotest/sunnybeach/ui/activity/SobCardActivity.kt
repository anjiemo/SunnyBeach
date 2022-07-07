package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SobCardActivityBinding
import cn.cqautotest.sunnybeach.ktx.startActivity
import com.dylanc.longan.intentExtras
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

    override fun getLayoutId() = R.layout.sob_card_activity

    override fun initView() {
        with(mBinding) {
            tvSobId.text = mSobId.manicured()
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