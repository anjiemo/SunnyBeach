package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.aop.SingleClick
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PasswordModifyActivityBinding
import cn.cqautotest.sunnybeach.ktx.lowercaseMd5
import cn.cqautotest.sunnybeach.manager.InputTextManager
import cn.cqautotest.sunnybeach.model.User
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.dialog.TipsDialog
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/05
 * desc   : 修改密码
 */
class PasswordModifyActivity : AppActivity(), TextView.OnEditorActionListener {

    private val mBinding by viewBinding<PasswordModifyActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()

    /**
     * 手机号
     */
    private var mPhoneNumber: String? = null

    /**
     * 验证码
     */
    private var mVerifyCode: String? = null

    override fun getLayoutId(): Int = R.layout.password_modify_activity

    override fun initView() {
        val commitView = mBinding.btnPasswordModifyCommit
        setOnClickListener(commitView)
        mBinding.etPasswordModifyPassword2.setOnEditorActionListener(this)
        InputTextManager.with(this)
            .addView(mBinding.etPasswordModifyPassword1)
            .addView(mBinding.etPasswordModifyPassword2)
            .setMain(commitView)
            .build()
    }

    override fun initData() {
        mPhoneNumber = getString(IntentKey.PHONE)
        mVerifyCode = getString(IntentKey.CODE)
        mBinding.etPasswordModifyPhone.setText(manicured(mPhoneNumber))
    }

    private fun manicured(phoneNum: String?): String =
        phoneNum?.replace("(\\d{3})(\\d{4})(\\d{4})".toRegex(), "$1****$2") ?: ""

    override fun onClick(view: View) {
        onSingleClick(view)
    }

    @SingleClick
    private fun onSingleClick(view: View) {
        val commitView = mBinding.btnPasswordModifyCommit
        val firstPassword = mBinding.etPasswordModifyPassword1
        val secondPassword = mBinding.etPasswordModifyPassword2
        if (view === commitView) {
            commitView.isEnabled = false
            if (firstPassword.text.toString() != secondPassword.text.toString()) {
                firstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                secondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_password_input_unlike)
                commitView.isEnabled = true
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)
            val phone = mPhoneNumber ?: ""
            val newPwd: String = firstPassword.text.toString()
            val smsCode = mVerifyCode ?: ""
            val user = User(phone, newPwd.lowercaseMd5, "")

            // 修改密码
            mUserViewModel.modifyPasswordBySms(smsCode, user).observe(this) { result ->
                commitView.isEnabled = true
                result.onSuccess {
                    TipsDialog.Builder(getContext())
                        .setIcon(TipsDialog.ICON_FINISH)
                        .setMessage(R.string.password_reset_success)
                        .setDuration(2000)
                        .addOnDismissListener { finish() }
                        .show()
                }.onFailure {
                    toast(it.message)
                }
            }
        }
    }

    /**
     * [TextView.OnEditorActionListener]
     */
    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        val commitView = mBinding.btnPasswordModifyCommit
        if (actionId == EditorInfo.IME_ACTION_DONE && commitView.isEnabled) {
            // 模拟点击提交按钮
            onClick(commitView)
            return true
        }
        return false
    }

    companion object {
        @Log
        fun start(context: Context, phone: String?, code: String?) {
            val intent = Intent(context, PasswordModifyActivity::class.java)
            intent.putExtra(IntentKey.PHONE, phone)
            intent.putExtra(IntentKey.CODE, code)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}