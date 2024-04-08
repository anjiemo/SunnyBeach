package cn.cqautotest.sunnybeach.util

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.util.EmojiMapHelper.getEmojiValue
import java.util.regex.Pattern

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/25
 * desc   : emoji 文字表情过滤器 [表情]
 */
class EmojiInputFilter : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        // 如果是删除操作，直接允许
        takeIf { end - start == 0 }?.let { return null }
        val sourceText = source.toString()
        val emojiMap = EmojiMapHelper.emojiMap
        val ssb = SpannableStringBuilder(source)
        for ((emoji) in emojiMap) {
            replaceEmojiTextByImg(emoji, sourceText, ssb)
        }
        return ssb
    }

    private fun replaceEmojiTextByImg(emoji: String?, sourceText: String?, ssb: SpannableStringBuilder) {
        emoji ?: return
        sourceText ?: return
        val pattern = Pattern.compile(emoji)
        val matcher = pattern.matcher(sourceText)
        var lastIndex = 0
        while (matcher.find()) {
            val startIndex = sourceText.indexOf(emoji, lastIndex)
            if (startIndex == -1) return
            val resId = getEmojiValue(emoji)
            val imageSpan = ImageSpanCompat.newImageSpan(AppApplication.getInstance(), resId, ImageSpanCompat.ALIGN_CENTER)
            lastIndex = startIndex + emoji.length
            ssb.setSpan(imageSpan, startIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            // Timber.d("filter：===> startIndex is %s lastIndex is %s", startIndex, lastIndex);
        }
    }
}