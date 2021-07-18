@file:JvmName("EditTextUtils")
package cn.cqautotest.sunnybeach.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

val TextView.textString
    get() = text.toString()

/**
 * 用于简化将 afterTextChanged 操作设置为 EditText 组件的扩展功能
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}