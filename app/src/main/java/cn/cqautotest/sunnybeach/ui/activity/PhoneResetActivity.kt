package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.http.api.other.GetCodeApi
import cn.cqautotest.sunnybeach.http.api.other.PhoneApi
import cn.cqautotest.sunnybeach.http.model.HttpData
import cn.cqautotest.sunnybeach.manager.InputTextManager
import cn.cqautotest.sunnybeach.ui.dialog.TipsDialog
import com.flyjingfish.android_aop_core.annotations.SingleClick
import com.hjq.base.BaseDialog
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.hjq.toast.Toaster
import com.hjq.widget.view.CountdownView

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/04/20
 *    desc   : 设置手机号
 */
class PhoneResetActivity : AppActivity(), OnEditorActionListener {

    companion object {

        private const val INTENT_KEY_IN_CODE: String = "code"

        @Log
        fun start(context: Context, code: String?) {
            val intent = Intent(context, PhoneResetActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_CODE, code)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val phoneView: EditText? by lazy { findViewById(R.id.et_phone_reset_phone) }
    private val codeView: EditText? by lazy { findViewById(R.id.et_phone_reset_code) }
    private val countdownView: CountdownView? by lazy { findViewById(R.id.cv_phone_reset_countdown) }
    private val commitView: Button? by lazy { findViewById(R.id.btn_phone_reset_commit) }

    /** 验证码 */
    private var verifyCode: String? = null

    override fun getLayoutId() = R.layout.phone_reset_activity

    override fun initView() {
        setOnClickListener(countdownView, commitView)
        codeView?.setOnEditorActionListener(this)
        commitView?.let {
            InputTextManager.with(this)
                .addView(phoneView)
                .addView(codeView)
                .setMain(it)
                .build()
        }
    }

    override fun initData() {
        verifyCode = getString(INTENT_KEY_IN_CODE)
    }

    @SingleClick
    override fun onClick(view: View) {
        if (view === countdownView) {
            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }
            if (true) {
                toast(R.string.common_code_send_hint)
                countdownView?.start()
                return
            }

            // 获取验证码
            EasyHttp.post(this)
                .api(GetCodeApi().apply {
                    setPhone(phoneView?.text.toString())
                })
                .request(object : HttpCallbackProxy<HttpData<Void?>>(this) {
                    override fun onHttpSuccess(result: HttpData<Void?>) {
                        toast(R.string.common_code_send_hint)
                        countdownView?.start()
                    }
                })

        } else if (view === commitView) {

            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }
            if (codeView?.text.toString().length != resources.getInteger(R.integer.sms_code_length)) {
                Toaster.show(R.string.common_code_error_hint)
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)
            if (true) {
                TipsDialog.Builder(this)
                    .setIcon(TipsDialog.ICON_FINISH)
                    .setMessage(R.string.phone_reset_commit_succeed)
                    .setDuration(2000)
                    .addOnDismissListener(object : BaseDialog.OnDismissListener {

                        override fun onDismiss(dialog: BaseDialog?) {
                            finish()
                        }
                    })
                    .show()
                return
            }

            // 更换手机号
            EasyHttp.post(this)
                .api(PhoneApi().apply {
                    setPreCode(verifyCode)
                    setPhone(phoneView?.text.toString())
                    setCode(codeView?.text.toString())
                })
                .request(object : HttpCallbackProxy<HttpData<Void?>>(this) {

                    override fun onHttpSuccess(result: HttpData<Void?>) {
                        TipsDialog.Builder(this@PhoneResetActivity)
                            .setIcon(TipsDialog.ICON_FINISH)
                            .setMessage(R.string.phone_reset_commit_succeed)
                            .setDuration(2000)
                            .addOnDismissListener {
                                finish()
                            }
                            .show()
                    }
                })
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