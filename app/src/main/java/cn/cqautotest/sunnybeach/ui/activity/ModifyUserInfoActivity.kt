package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ModifyUserInfoActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.PersonCenterInfo
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.dylanc.longan.intentExtras
import com.dylanc.longan.textString
import com.hjq.bar.TitleBar

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/21
 * desc   : 修改用户信息界面
 */
class ModifyUserInfoActivity : AppActivity() {

    private val mBinding by viewBinding<ModifyUserInfoActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()
    private val modifyTypeJson by intentExtras<String>(MODIFY_TYPE)
    private val modifyType by lazy { fromJson<ModifyType>(modifyTypeJson) }
    private var mPersonCenterInfo: PersonCenterInfo? = null

    override fun getLayoutId(): Int = R.layout.modify_user_info_activity

    override fun initView() {
        mBinding.titleBar.title = modifyType.title
        mBinding.etInputContent.hint = when (modifyType) {
            ModifyType.COMPANY -> "xxx科技/网络/技术有限公司"
            ModifyType.JOB -> "xxx工程师/设计师/技术专家"
            ModifyType.SKILL -> "Kotlin/Java/安卓/iOS/Vue/算法/Ai"
            ModifyType.SIGN -> "专业写 BUG 20年..."
            else -> error("We can't supported modify this type.")
        }
    }

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        val etInputContent = mBinding.etInputContent
        etInputContent.doAfterTextChanged {
            mBinding.tvDesc.text = "当前已输入 ${it?.length ?: 0} 字"
        }
        etInputContent.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> hideKeyboard()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        queryUserInfo()
    }

    override fun onRightClick(titleBar: TitleBar) {
        super.onRightClick(titleBar)
        submitModify()
    }

    private fun submitModify() {
        val personCenterInfo = mPersonCenterInfo ?: return
        val text = mBinding.etInputContent.textString
        val modifiedPersonCenterInfo = when (modifyType) {
            ModifyType.COMPANY -> personCenterInfo.copy(company = text)
            ModifyType.JOB -> personCenterInfo.copy(position = text)
            ModifyType.SKILL -> personCenterInfo.copy(goodAt = text)
            ModifyType.SIGN -> personCenterInfo.copy(sign = text)
            else -> error("We can't supported modify this type.")
        }
        mUserViewModel.modifyUserInfo(modifiedPersonCenterInfo).observe(this) { result ->
            takeIf { result.getOrNull() == true }?.let {
                simpleToast("修改成功")
                finish()
            } ?: simpleToast("修改失败")
        }
    }

    private fun queryUserInfo() {
        mUserViewModel.queryUserInfo().observe(this) { result ->
            val personCenterInfo = result.getOrNull()
            mPersonCenterInfo = personCenterInfo
            val text = when (modifyType) {
                ModifyType.COMPANY -> personCenterInfo?.company
                ModifyType.JOB -> personCenterInfo?.position
                ModifyType.SKILL -> personCenterInfo?.goodAt
                ModifyType.SIGN -> personCenterInfo?.sign
                else -> error("We can't supported modify this type.")
            }
            mBinding.etInputContent.apply {
                setText(text)
                setSelection(length)
            }
        }
    }

    enum class ModifyType(val title: String) {
        COMPANY("公司"),
        JOB("职位"),
        SKILL("技能"),
        SIGN("签名"),
    }

    companion object {

        private const val MODIFY_TYPE = "modify_type"

        fun start(context: Context, modifyType: ModifyType) {
            context.startActivity<ModifyUserInfoActivity> {
                putExtra(MODIFY_TYPE, modifyType.toJson())
            }
        }
    }
}