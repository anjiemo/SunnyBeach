package cn.cqautotest.sunnybeach.ui.activity

import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.RegisterActivityBinding
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.lowercaseMd5
import cn.cqautotest.sunnybeach.manager.InputTextManager
import cn.cqautotest.sunnybeach.model.SmsInfo
import cn.cqautotest.sunnybeach.model.User
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.util.VERIFY_CODE_URL
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.flyjingfish.android_aop_core.annotations.SingleClick
import com.gyf.immersionbar.ImmersionBar
import com.hjq.base.BaseActivity
import com.hjq.widget.view.CountdownView
import com.hjq.widget.view.SubmitButton

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 注册界面
 */
class RegisterActivity : AppActivity(), OnEditorActionListener {

    companion object {

        private const val INTENT_KEY_PHONE: String = "phone"
        private const val INTENT_KEY_PASSWORD: String = "password"

        @Log
        fun start(activity: BaseActivity, phone: String?, password: String?, listener: OnRegisterListener?) {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.putExtra(INTENT_KEY_PHONE, phone)
            intent.putExtra(INTENT_KEY_PASSWORD, password)
            activity.startActivityForResult(intent, object : OnActivityCallback {

                override fun onActivityResult(resultCode: Int, data: Intent?) {
                    if (listener == null || data == null) {
                        return
                    }
                    if (resultCode == RESULT_OK) {
                        listener.onSucceed(data.getStringExtra(INTENT_KEY_PHONE), data.getStringExtra(INTENT_KEY_PASSWORD))
                    } else {
                        listener.onCancel()
                    }
                }
            })
        }
    }

    private val mBinding by viewBinding<RegisterActivityBinding>()
    private val phoneView: EditText? by lazy { findViewById(R.id.et_register_phone) }
    private val countdownView: CountdownView? by lazy { findViewById(R.id.cv_register_countdown) }
    private val codeView: EditText? by lazy { findViewById(R.id.et_register_code) }
    private val firstPassword: EditText? by lazy { findViewById(R.id.et_register_password1) }
    private val secondPassword: EditText? by lazy { findViewById(R.id.et_register_password2) }
    private val commitView: SubmitButton? by lazy { findViewById(R.id.btn_register_commit) }

    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId() = R.layout.register_activity

    override fun initView() {
        setOnClickListener(mBinding.sivRegisterVerifyCode, countdownView, commitView)
        secondPassword?.setOnEditorActionListener(this)

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title))
        commitView?.let {
            InputTextManager.with(this)
                .addView(mBinding.etRegisterVerifyCode)
                .addView(phoneView)
                .addView(codeView)
                .addView(mBinding.etRegisterNickName)
                .addView(firstPassword)
                .addView(secondPassword)
                .setMain(it)
                .build()
        }
    }

    override fun initData() {
        loadVerifyCode()
        // 自动填充手机号和密码
        phoneView?.setText(getString(INTENT_KEY_PHONE))
        firstPassword?.setText(getString(INTENT_KEY_PASSWORD))
        secondPassword?.setText(getString(INTENT_KEY_PASSWORD))
    }

    /**
     * 加载验证码图片
     */
    private fun loadVerifyCode() {
        GlideApp.with(this)
            .load(VERIFY_CODE_URL)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(mBinding.sivRegisterVerifyCode)
    }

    override fun onClick(view: View) {
        if (view === mBinding.sivRegisterVerifyCode) {
            loadVerifyCode()
            return
        }
        onSingleClick(view)
    }

    @SingleClick
    private fun onSingleClick(view: View) {
        if (view === countdownView) {
            val etRegisterVerifyCode = mBinding.etRegisterVerifyCode
            if (TextUtils.isEmpty(etRegisterVerifyCode.text)) {
                etRegisterVerifyCode.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_verify_code_input_hint)
                return
            }

            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }

            val smsInfo = SmsInfo(phoneView?.text.toString(), mBinding.etRegisterVerifyCode.text.toString())
            // 发送验证码
            mUserViewModel.sendRegisterSmsVerifyCode(smsInfo).observe(this) { result ->
                result.onSuccess {
                    toast(it)
                }.onFailure {
                    toast(it.message)
                }
                countdownView?.start()
            }

        } else if (view === commitView) {

            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                commitView?.showError(3000)
                toast(R.string.common_phone_input_error)
                return
            }

            if (codeView?.text.toString().length != resources.getInteger(R.integer.sms_code_length)) {
                codeView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                commitView?.showError(3000)
                toast(R.string.common_code_error_hint)
                return
            }

            if (firstPassword?.text.toString() != secondPassword?.text.toString()) {
                firstPassword?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                secondPassword?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                commitView?.showError(3000)
                toast(R.string.common_password_input_unlike)
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)

            commitView?.showProgress()

            val phoneNum: String = phoneView?.text.toString()
            val verifyCode: String = codeView?.text.toString()
            val nickName: String = mBinding.etRegisterNickName.text.toString()
            val password: String = firstPassword?.text.toString()

            val user = User(phoneNum, password.lowercaseMd5, nickName)

            mUserViewModel.registerAccount(verifyCode, user).observe(this) { result ->
                postDelayed({
                    result.onSuccess {
                        toast(it)
                        commitView?.showSucceed()
                        val intent = Intent().apply {
                            putExtra(IntentKey.PHONE, phoneView?.text.toString())
                            putExtra(IntentKey.PASSWORD, firstPassword?.text.toString())
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }.onFailure {
                        toast(it.message)
                        commitView?.showError(3000)
                    }
                }, 1000)
            }
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // 指定导航栏背景颜色
            .navigationBarColor(R.color.white) // 不要把整个布局顶上去
            .keyboardEnable(true)
    }

    override fun isStatusBarDarkFont() = true

    /**
     * [TextView.OnEditorActionListener]
     */
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // 模拟点击注册按钮
            commitView?.let {
                if (it.isEnabled) {
                    onClick(it)
                }
            }
            return true
        }
        return false
    }

    /**
     * 注册监听
     */
    interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone             手机号
         * @param password          密码
         */
        fun onSucceed(phone: String?, password: String?)

        /**
         * 取消注册
         */
        fun onCancel() {}
    }
}