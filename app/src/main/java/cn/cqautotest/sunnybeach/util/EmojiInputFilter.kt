package cn.cqautotest.sunnybeach.util

import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import cn.cqautotest.sunnybeach.app.AppApplication

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/25
 * desc   : emoji 文字表情过滤器 [表情]
 */
class EmojiInputFilter : InputFilter {

    /**
     * 过滤输入的文本，将匹配的emoji短代码替换为对应的图片
     *
     * [source] 新输入的字符序列
     * [start] 新输入字符的起始位置
     * [end] 新输入字符的结束位置
     * [dest] 目标编辑框中已有的文本
     * [dstart] 新输入字符在目标文本中的起始位置
     * [dend] 新输入字符在目标文本中的结束位置
     * @return 如果进行了emoji替换则返回带有图片的SpannableStringBuilder，否则返回null表示接受原始输入
     */
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val inputText = source.subSequence(start, end)

        if (inputText.isEmpty()) {
            return null
        }

        return processEmojiReplacement(inputText)
    }

    /**
     * 处理emoji替换逻辑
     */
    private fun processEmojiReplacement(inputText: CharSequence): CharSequence? {
        val builder = SpannableStringBuilder(inputText)
        var changed = false
        var currentText = builder.toString()

        for ((emoji) in EmojiMapHelper.emojiMap) {
            val result = replaceEmojiInBuilder(builder, emoji, currentText)
            if (result.changed) {
                changed = true
                currentText = result.updatedText
            }
        }

        return if (changed) builder else null
    }

    /**
     * 在builder中替换指定emoji
     */
    private fun replaceEmojiInBuilder(builder: SpannableStringBuilder, emoji: String, currentText: String): ReplacementResult {
        var updatedText = currentText
        var changed = false
        var lastIndex = 0
        val emojiLength = emoji.length

        while (lastIndex <= updatedText.length - emojiLength) {
            val index = updatedText.indexOf(emoji, lastIndex)

            if (index == -1) {
                break
            }

            changed = true
            val imageSpan = createEmojiImageSpan(emoji)
            builder.setSpan(imageSpan, index, index + emojiLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            lastIndex = index + emojiLength
            updatedText = builder.toString()
        }

        return ReplacementResult(changed, updatedText)
    }

    /**
     * 创建emoji图片span
     */
    private fun createEmojiImageSpan(emoji: String): ImageSpan {
        val resId = EmojiMapHelper.getEmojiValue(emoji)
        return ImageSpanCompat.newImageSpan(AppApplication.getInstance(), resId, ImageSpanCompat.ALIGN_CENTER)
    }

    /**
     * 替换结果数据类
     */
    private data class ReplacementResult(val changed: Boolean, val updatedText: String)
}