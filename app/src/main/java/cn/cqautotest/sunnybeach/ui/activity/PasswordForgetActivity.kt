package cn.cqautotest.sunnybeach.ui.activity

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
import cn.cqautotest.sunnybeach.aop.SingleClick
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.PasswordForgetActivityBinding
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.manager.InputTextManager
import cn.cqautotest.sunnybeach.model.SmsInfo
import cn.cqautotest.sunnybeach.util.VERIFY_CODE_URL
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hjq.widget.view.CountdownView

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/02/27
 *    desc   : 忘记密码
 */
class PasswordForgetActivity : AppActivity(), OnEditorActionListener {

    private val mBinding by viewBinding<PasswordForgetActivityBinding>()
    private val phoneView: EditText? by lazy { findViewById(R.id.et_password_forget_phone) }
    private val codeView: EditText? by lazy { findViewById(R.id.et_password_forget_code) }
    private val countdownView: CountdownView? by lazy { findViewById(R.id.cv_password_forget_countdown) }
    private val commitView: Button? by lazy { findViewById(R.id.btn_password_forget_commit) }

    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.password_forget_activity
    }

    override fun initView() {
        setOnClickListener(countdownView, commitView)
        codeView?.setOnEditorActionListener(this)
        commitView?.let {
            InputTextManager.with(this)
                .addView(mBinding.etPasswordForgetVerifyCode)
                .addView(phoneView)
                .addView(codeView)
                .setMain(it)
                .build()
        }
    }

    override fun initData() {
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
            .into(mBinding.sivPasswordForgetVerifyCode)
    }

    override fun onClick(view: View) {
        if (view === mBinding.etPasswordForgetVerifyCode) {
            loadVerifyCode()
            return
        }
        onSingleClick(view)
    }

    @SingleClick
    private fun onSingleClick(view: View) {
        val etPasswordForgetVerifyCode = mBinding.etPasswordForgetVerifyCode
        if (view === countdownView) {
            if (TextUtils.isEmpty(etPasswordForgetVerifyCode.text)) {
                etPasswordForgetVerifyCode.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_verify_code_input_hint)
                return
            }

            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)

            val smsInfo = SmsInfo(phoneView?.text.toString(), mBinding.etPasswordForgetVerifyCode.text.toString())

            // 发送验证码
            mUserViewModel.sendForgetSmsVerifyCode(smsInfo).observe(this) { result ->
                countdownView?.start()
                val message = result.getOrNull()
                toast(message)
            }
        } else if (view === commitView) {
            if (TextUtils.isEmpty(etPasswordForgetVerifyCode.text)) {
                etPasswordForgetVerifyCode.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_verify_code_input_hint)
                return
            }

            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }

            if (codeView?.text.toString().length != resources.getInteger(R.integer.sms_code_length)) {
                codeView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_code_error_hint)
                return
            }

            val phoneNumber: String = phoneView?.text.toString()
            val smsCode: String = codeView?.text.toString()

            // 验证码校验
            mUserViewModel.checkSmsCode(phoneNumber, smsCode).observe(this) { result ->
                val message = result.getOrNull() ?: run {
                    toast("验证码校验失败")
                    return@observe
                }
                toast(message)
                PasswordModifyActivity.start(getContext(), phoneNumber, smsCode)
                finish()
            }
        }
    }

    /**
     * [TextView.OnEditorActionListener]
     */
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // 模拟点击下一步按钮
            commitView?.let {
                if (it.isEnabled) {
                    onClick(it)
                    return true
                }
            }
        }
        return false
    }
}