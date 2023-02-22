package cn.cqautotest.sunnybeach.ktx

import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.webkit.URLUtil
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.getSpans
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.util.EmojiInputFilter

fun TextView.interceptHyperLinkClickFrom(spanned: Spanned) {
    movementMethod = LinkMovementMethod.getInstance()
    val urlSpans = spanned.getSpans<URLSpan>()
    text = buildSpannedString {
        append(spanned)
        for (urlSpan in urlSpans) {
            val url = urlSpan.url
            if (URLUtil.isNetworkUrl(url)) {
                val customUrlSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        BrowserActivity.start(context, url)
                    }
                }
                setSpan(customUrlSpan, spanned.getSpanStart(urlSpan), spanned.getSpanEnd(urlSpan), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
    }
}

fun TextView.setDefaultEmojiParser() {
    filters = Array(1) { EmojiInputFilter() }
}

fun TextView.addDefaultEmojiParser() {
    val emojiInputFilter = EmojiInputFilter()
    filters = if (filters.isEmpty()) {
        // 如果没有表情输入过滤器，则添加一个
        Array(1) { emojiInputFilter }
    } else {
        // 如果已经有输入过滤器了，则在原有的输入过滤器之外额外添加一个表情输入过滤器
        val filterList = filters.toMutableList()
        filterList.add(emojiInputFilter)
        filterList.toTypedArray()
    }
}

fun TextView.clearText() {
    text = null
}

val TextView.textString: String
    get() = if (text != null) text.toString() else ""

val TextView.length: Int
    get() = length()
