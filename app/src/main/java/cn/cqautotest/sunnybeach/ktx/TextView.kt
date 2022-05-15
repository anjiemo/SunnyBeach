package cn.cqautotest.sunnybeach.ktx

import android.widget.TextView
import cn.cqautotest.sunnybeach.util.EmojiInputFilter

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
