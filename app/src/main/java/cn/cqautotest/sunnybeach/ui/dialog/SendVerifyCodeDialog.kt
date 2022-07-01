package cn.cqautotest.sunnybeach.ui.dialog

import android.content.Context
import android.view.View
import android.widget.EditText
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.SingleClick
import com.hjq.base.BaseDialog
import com.hjq.toast.ToastUtils
import com.hjq.widget.view.CountdownView
import com.hjq.widget.view.RegexEditText

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/02/06
 *    desc   : 身份校验对话框
 */
class SendVerifyCodeDialog {

    class Builder(context: Context) : CommonDialog.Builder<Builder>(context) {

        private val inputView: RegexEditText? by lazy { findViewById(R.id.tv_safe_input) }
        private val codeView: EditText? by lazy { findViewById(R.id.et_safe_code) }
        private val countdownView: CountdownView? by lazy { findViewById(R.id.cv_safe_countdown) }

        private var listener: OnListener? = null

        /** 当前输入的内容 */
        private val phoneNumber: String = ""

        init {
            setTitle(R.string.safe_title)
            setCustomView(R.layout.safe_dialog)
            setOnClickListener(countdownView)
        }

        fun setCode(code: String?): Builder = apply {
            codeView?.setText(code)
        }

        fun setRegex(regex: String): Builder = apply {
            inputView?.setInputRegex(regex)
        }

        fun setListener(listener: OnListener?): Builder = apply {
            this.listener = listener
        }

        fun setListener(
            onSendVerifyCode: (dialog: BaseDialog?, inputContent: String) -> Unit,
            onConfirm: (dialog: BaseDialog?, inputContent: String, code: String) -> Unit
        ) = apply {
            this.listener = object : OnListener {
                override fun onSendVerifyCode(dialog: BaseDialog?, inputContent: String) {
                    onSendVerifyCode.invoke(dialog, inputContent)
                }

                override fun onConfirm(dialog: BaseDialog?, inputContent: String, code: String) {
                    onConfirm.invoke(dialog, inputContent, code)
                }
            }
        }

        @SingleClick
        override fun onClick(view: View) {
            when (view.id) {
                R.id.cv_safe_countdown -> {
                    ToastUtils.show(R.string.common_code_send_hint)
                    countdownView?.start()
                    setCancelable(false)
                    listener?.onSendVerifyCode(getDialog(), inputView?.text.toString())
                }
                R.id.tv_ui_confirm -> {
                    if (codeView?.text.toString().length != getResources().getInteger(R.integer.sms_code_length)) {
                        ToastUtils.show(R.string.common_code_error_hint)
                        return
                    }
                    listener?.onConfirm(getDialog(), phoneNumber, codeView?.text.toString())
                }
                R.id.tv_ui_cancel -> {
                    autoDismiss()
                    listener?.onCancel(getDialog())
                }
            }
        }
    }

    interface OnListener {

        /**
         * 点击发送验证码时回调
         */
        fun onSendVerifyCode(dialog: BaseDialog?, inputContent: String)

        /**
         * 点击确定时回调
         */
        fun onConfirm(dialog: BaseDialog?, inputContent: String, code: String)

        /**
         * 点击取消时回调
         */
        fun onCancel(dialog: BaseDialog?) {}
    }
}