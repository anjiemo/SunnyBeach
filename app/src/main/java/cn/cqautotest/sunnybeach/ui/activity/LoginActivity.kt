package cn.cqautotest.sunnybeach.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.LoginActivityBinding
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.clearText
import cn.cqautotest.sunnybeach.ktx.textString
import cn.cqautotest.sunnybeach.manager.InputTextManager
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.other.KeyboardWatcher
import cn.cqautotest.sunnybeach.util.VERIFY_CODE_URL
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.wxapi.WXEntryActivity
import com.blankj.utilcode.util.RegexUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.flyjingfish.android_aop_core.annotations.SingleClick
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.TitleBar
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengClient
import com.hjq.umeng.UmengLogin
import com.hjq.widget.view.SubmitButton

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 登录界面
 */
class LoginActivity : AppActivity(), UmengLogin.OnLoginListener,
    KeyboardWatcher.SoftKeyboardStateListener, TextView.OnEditorActionListener {

    companion object {

        private const val FROM_HOME = "from_home"
        private const val INTENT_KEY_IN_PHONE: String = "phone"
        private const val INTENT_KEY_IN_PASSWORD: String = "password"

        @Log
        fun createIntent(
            context: Context,
            phone: String? = UserManager.getCurrLoginAccount(),
            password: String? = UserManager.getCurrLoginAccountPassword()
        ) = Intent(context, LoginActivity::class.java).apply {
            putExtra(FROM_HOME, (context as? HomeActivity) != null)
            putExtra(INTENT_KEY_IN_PHONE, phone)
            putExtra(INTENT_KEY_IN_PASSWORD, password)
        }

        fun start(context: Context, phone: String?, password: String?) {
            context.startActivity(createIntent(context, phone, password))
        }
    }

    private val mBinding by viewBinding<LoginActivityBinding>()
    private val logoView: ImageView? by lazy { findViewById(R.id.iv_login_logo) }
    private val bodyLayout: ViewGroup? by lazy { findViewById(R.id.ll_login_body) }
    private val phoneView: EditText? by lazy { findViewById(R.id.et_login_phone) }
    private val passwordView: EditText? by lazy { findViewById(R.id.et_login_password) }
    private val forgetView: View? by lazy { findViewById(R.id.tv_login_forget) }
    private val commitView: SubmitButton? by lazy { findViewById(R.id.btn_login_commit) }
    private val otherView: View? by lazy { findViewById(R.id.ll_login_other) }
    private val qqView: View? by lazy { findViewById(R.id.iv_login_qq) }
    private val weChatView: View? by lazy { findViewById(R.id.iv_login_wechat) }

    /** logo 缩放比例 */
    private val logoScale: Float = 0.8f

    /** 动画时间 */
    private val animTime: Int = 300

    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId() = R.layout.login_activity

    override fun initView() {
        with(mBinding) {
            setOnClickListener(sivLoginVerifyCode, forgetView, commitView, qqView, weChatView)
            etLoginVerifyCode.setOnEditorActionListener(this@LoginActivity)
            cbRememberPwd.isChecked = UserManager.isRememberPwd()
            commitView?.let {
                InputTextManager.with(this@LoginActivity)
                    .addView(phoneView)
                    .addView(passwordView)
                    .addView(etLoginVerifyCode)
                    .setMain(it)
                    .build()
            }
        }
    }

    override fun initData() {
        // 首次进入时刷新验证码
        loadVerifyCode()
        postDelayed({
            KeyboardWatcher.with(this@LoginActivity)
                .setListener(this@LoginActivity)
        }, 500)

        // 判断用户当前有没有安装 QQ
        if (!UmengClient.isAppInstalled(this, Platform.QQ)) {
            qqView?.visibility = View.GONE
        }

        // 判断用户当前有没有安装微信
        if (!UmengClient.isAppInstalled(this, Platform.WECHAT)) {
            weChatView?.visibility = View.GONE
        }

        // 如果这两个都没有安装就隐藏提示
        if (qqView?.visibility == View.GONE && weChatView?.visibility == View.GONE) {
            otherView?.visibility = View.GONE
        }

        // 自动填充手机号和密码
        phoneView?.let {
            it.setText(getString(INTENT_KEY_IN_PHONE))
            it.setSelection(it.length())
        }
        passwordView?.setText(getString(INTENT_KEY_IN_PASSWORD))
        tryLoadUserAvatar()
    }

    /**
     * 尝试加载用户头像，如果用户账号不是手机号则不查询头像
     */
    private fun tryLoadUserAvatar() {
        UserManager.getCurrLoginAccount().takeIf { RegexUtils.isMobileExact(it) }
            ?.let { mUserViewModel.queryUserAvatar(it) }
    }

    override fun initEvent() {
        phoneView?.doAfterTextChanged { result ->
            if (TextUtils.isEmpty(result)) {
                commitView?.reset()
            }
            if (RegexUtils.isMobileExact(result)) {
                mUserViewModel.queryUserAvatar(result.toString())
            } else {
                Glide.with(this)
                    .load(R.mipmap.ic_default_avatar)
                    .into(mBinding.ivLoginLogo)
            }
        }
        passwordView?.doAfterTextChanged { result ->
            if (TextUtils.isEmpty(result)) {
                commitView?.reset()
            }
        }
        with(mBinding) {
            etLoginVerifyCode.doAfterTextChanged { result ->
                if (TextUtils.isEmpty(result)) {
                    commitView?.reset()
                }
            }
            cbRememberPwd.setOnCheckedChangeListener { _, isChecked -> UserManager.isRememberPwd(isChecked) }
        }
    }

    override fun initObserver() {
        mUserViewModel.userAvatarLiveData.observe(this) { result ->
            Glide.with(this)
                .load(result.getOrNull())
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(mBinding.ivLoginLogo)
        }
    }

    /**
     * 加载验证码图片
     */
    private fun loadVerifyCode() {
        GlideApp.with(this)
            .load(VERIFY_CODE_URL)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(mBinding.sivLoginVerifyCode)
    }

    override fun onLeftClick(titleBar: TitleBar) {
        // 用户点击了跳过按钮
        HomeActivity.start(this)
        finish()
    }

    override fun onRightClick(titleBar: TitleBar) {
        // 跳转到注册界面
        RegisterActivity.start(
            this, phoneView?.textString, "",
            object : RegisterActivity.OnRegisterListener {

                override fun onSucceed(phone: String?, password: String?) {
                    // 如果已经注册成功，就执行登录操作（需要用户先输入验证码）
                    phoneView?.setText(phone)
                    passwordView?.setText(password)
                    passwordView?.requestFocus()
                    passwordView?.setSelection(passwordView?.text.toString().length)
                    // commitView?.let { onClick(it) }
                }
            }
        )
    }

    override fun onClick(view: View) {
        if (view === mBinding.sivLoginVerifyCode) {
            loadVerifyCode()
            return
        }
        onSingleClick(view)
    }

    @SingleClick
    private fun onSingleClick(view: View) {
        if (view === forgetView) {
            startActivity(PasswordForgetActivity::class.java)
            return
        }
        if (view === commitView) {
            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                commitView?.showError(3000)
                toast(R.string.common_phone_input_error)
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)

            commitView?.showProgress()
            // 用户账号
            val userAccount = phoneView?.text.toString()
            // 用户密码
            val userPassword = passwordView?.text.toString()
            // 验证码
            val verifyCode = mBinding.etLoginVerifyCode.text.toString()
            // 登录
            login(userAccount, userPassword, verifyCode)
            return
        }
        if (view === qqView || view === weChatView) {
            if (true) {
                toast("暂未接入其他登录方式")
                return
            }
            toast("记得改好第三方 AppID 和 Secret，否则会调不起来哦")
            val platform: Platform?
            when {
                view === qqView -> {
                    platform = Platform.QQ
                }

                view === weChatView -> {
                    if (packageName.endsWith(".debug")) {
                        toast("当前 buildType 不支持进行微信登录")
                        return
                    }
                    platform = Platform.WECHAT
                    toast("也别忘了改微信 " + WXEntryActivity::class.java.simpleName + " 类所在的包名哦")
                }

                else -> {
                    throw IllegalStateException("are you ok?")
                }
            }
            UmengClient.login(this, platform, this)
        }
    }

    /**
     * 登录账号
     * userAccount：用户账号（手机号）
     * userPassword：用户密码（不低于5位的密码）
     * verifyCode：人类验证码（图片验证码）
     */
    private fun login(userAccount: String, userPassword: String, verifyCode: String) {
        mUserViewModel.login(userAccount, userPassword, verifyCode).observe(this) { result ->
            result.onSuccess { userBasicInfo ->
                // 将基本信息缓存到本地
                UserManager.saveUserBasicInfo(userBasicInfo)
                // 默认记住账号
                UserManager.saveCurrLoginAccount(userAccount)
                // 是否记住密码
                val rememberPwd = mBinding.cbRememberPwd.isChecked
                UserManager.saveCurrLoginAccountPassword(if (rememberPwd) userPassword else "")
                commitView?.showSucceed()
                // 延迟跳转到首页，等待动画展示完成
                postDelayed({
                    setResult(Activity.RESULT_OK)
                    finish()
                }, 1000)
            }.onFailure {
                // 设置为非自动登录状态
                UserManager.setupAutoLogin(false)
                // 重新加载人类验证码
                loadVerifyCode()
                // 清空验证码输入
                mBinding.etLoginVerifyCode.clearText()
                // 提示错误信息
                toast(it.message)
                commitView?.showError()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 友盟回调
        UmengClient.onActivityResult(this, requestCode, resultCode, data)
    }

    /**
     * [UmengLogin.OnLoginListener]
     */
    /**
     * 授权成功的回调
     *
     * @param platform      平台名称
     * @param data          用户资料返回
     */
    override fun onSucceed(platform: Platform?, data: UmengLogin.LoginData?) {
        if (isFinishing || isDestroyed) {
            // Glide：You cannot start a load for a destroyed activity
            return
        }
        when (platform) {
            Platform.QQ -> {

            }

            Platform.WECHAT -> {

            }

            else -> {}
        }

        logoView?.let {
            GlideApp.with(this)
                .load(data?.getAvatar())
                .circleCrop()
                .into(it)
        }

        toast(
            ("昵称：" + data?.getName() + "\n" +
                    "性别：" + data?.getSex() + "\n" +
                    "id：" + data?.getId() + "\n" +
                    "token：" + data?.getToken())
        )
    }

    /**
     * 授权失败的回调
     *
     * @param platform      平台名称
     * @param t             错误原因
     */
    override fun onError(platform: Platform?, t: Throwable) {
        toast("第三方登录出错：" + t.message)
    }

    /**
     * [KeyboardWatcher.SoftKeyboardStateListener]
     */
    override fun onSoftKeyboardOpened(keyboardHeight: Int) {
        // 执行位移动画
        bodyLayout?.let {
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
                it,
                "translationY", 0f, (-(commitView?.height?.toFloat() ?: 0f))
            )
            objectAnimator.duration = animTime.toLong()
            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
            objectAnimator.start()
        }

        // 执行缩小动画
        logoView?.let {
            it.pivotX = it.width / 2f
            it.pivotY = it.height.toFloat()
            val animatorSet = AnimatorSet()
            val scaleX = ObjectAnimator.ofFloat(it, "scaleX", 1f, logoScale)
            val scaleY = ObjectAnimator.ofFloat(it, "scaleY", 1f, logoScale)
            val translationY = ObjectAnimator.ofFloat(
                it, "translationY",
                0f, (-(commitView?.height?.toFloat() ?: 0f))
            )
            animatorSet.play(translationY).with(scaleX).with(scaleY)
            animatorSet.duration = animTime.toLong()
            animatorSet.start()
        }
    }

    override fun onSoftKeyboardClosed() {
        // 执行位移动画
        bodyLayout?.let {
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
                it,
                "translationY", it.translationY, 0f
            )
            objectAnimator.duration = animTime.toLong()
            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
            objectAnimator.start()
        }

        // 执行放大动画
        logoView?.let {
            it.pivotX = it.width / 2f
            it.pivotY = it.height.toFloat()

            if (it.translationY == 0f) {
                return
            }

            val animatorSet = AnimatorSet()
            val scaleX: ObjectAnimator = ObjectAnimator.ofFloat(it, "scaleX", logoScale, 1f)
            val scaleY: ObjectAnimator = ObjectAnimator.ofFloat(it, "scaleY", logoScale, 1f)
            val translationY: ObjectAnimator = ObjectAnimator.ofFloat(
                it,
                "translationY", it.translationY, 0f
            )
            animatorSet.play(translationY).with(scaleX).with(scaleY)
            animatorSet.duration = animTime.toLong()
            animatorSet.start()
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
                    // 模拟点击登录按钮
                    onClick(it)
                    return true
                }
            }
        }
        return false
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // 指定导航栏背景颜色
            .navigationBarColor(R.color.white)
    }

    override fun isStatusBarDarkFont() = true
}