package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.aop.SingleClick
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PasswordResetActivityBinding
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.lowercaseMd5
import cn.cqautotest.sunnybeach.manager.InputTextManager
import cn.cqautotest.sunnybeach.model.ModifyPwd
import cn.cqautotest.sunnybeach.ui.dialog.TipsDialog
import cn.cqautotest.sunnybeach.util.VERIFY_CODE_URL
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/02/27
 *    desc   : 重置密码
 */
class PasswordResetActivity : AppActivity(), OnEditorActionListener {

    companion object {

        private const val INTENT_KEY_IN_PHONE: String = "phone"
        private const val INTENT_KEY_IN_CODE: String = "code"

        @Log
        fun start(context: Context, phone: String?, code: String?) {
            val intent = Intent(context, PasswordResetActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_PHONE, phone)
            intent.putExtra(INTENT_KEY_IN_CODE, code)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val mBinding by viewBinding<PasswordResetActivityBinding>()
    private val firstPassword: EditText? by lazy { findViewById(R.id.et_password_reset_password1) }
    private val secondPassword: EditText? by lazy { findViewById(R.id.et_password_reset_password2) }
    private val commitView: Button? by lazy { findViewById(R.id.btn_password_reset_commit) }

    /** 手机号 */
    private var phoneNumber: String? = null

    /** 验证码 */
    private var verifyCode: String? = null

    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId() = R.layout.password_reset_activity

    override fun initView() {
        setOnClickListener(commitView)
        secondPassword?.setOnEditorActionListener(this)
        commitView?.let {
            InputTextManager.with(this)
                .addView(mBinding.etPasswordResetVerifyCode)
                .addView(mBinding.etPasswordResetOldPassword)
                .addView(firstPassword)
                .addView(secondPassword)
                .setMain(it)
                .build()
        }
    }

    override fun initData() {
        phoneNumber = getString(INTENT_KEY_IN_PHONE)
        verifyCode = getString(INTENT_KEY_IN_CODE)
        loadVerifyCode()
    }

    /**
     * 加载验证码图片
     */
    private fun loadVerifyCode() {
        GlideApp.with(this)
            .load(VERIFY_CODE_URL)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(mBinding.sivPasswordResetVerifyCode)
    }

    override fun onClick(view: View) {
        if (view === mBinding.sivPasswordResetVerifyCode) {
            loadVerifyCode()
            return
        }
        onSingleClick(view)
    }

    @SingleClick
    private fun onSingleClick(view: View) {
        if (view === commitView) {
            val oldPassword = mBinding.etPasswordResetOldPassword
            if (TextUtils.isEmpty(oldPassword.text)) {
                oldPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.password_reset_phone_old_password_hint)
                return
            }

            if (firstPassword?.text.toString() != secondPassword?.text.toString()) {
                firstPassword?.startAnimation(
                    AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.shake_anim
                    )
                )
                secondPassword?.startAnimation(
                    AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.shake_anim
                    )
                )
                toast(R.string.common_password_input_unlike)
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)

            val oldPwd: String = oldPassword.text.toString()
            val newPwd: String = firstPassword?.text.toString()
            val captcha: String = mBinding.etPasswordResetVerifyCode.text.toString()

            val modifyPwd = ModifyPwd(oldPwd, newPwd.lowercaseMd5, captcha)

            // 修改密码
            mUserViewModel.modifyPasswordByOldPwd(modifyPwd).observe(this) { result ->
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
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // 模拟点击提交按钮
            commitView?.let {
                if (it.isEnabled) {
                    onClick(it)
                    return true
                }
            }
        }
        return false
    }

    override fun isStatusBarDarkFont() = false
}