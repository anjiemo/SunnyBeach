package cn.cqautotest.sunnybeach.util

import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
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
        // 1. 获取用户尝试插入的文本部分
        val inputText = source.subSequence(start, end)

        // 2. 如果输入为空，则不需要处理
        if (inputText.isEmpty()) {
            return null
        }

        // 3. 创建一个 SpannableStringBuilder 来进行替换操作
        val builder = SpannableStringBuilder(inputText)
        var changed = false

        // 4. 遍历所有 emoji 短代码进行匹配和替换
        for ((emoji) in EmojiMapHelper.emojiMap) {
            var lastIndex = 0

            // 使用循环查找所有匹配项
            while (lastIndex < builder.length) {
                // 在当前 builder 中查找 emoji 短代码的完整匹配
                val index = builder.toString().indexOf(emoji, lastIndex)

                if (index == -1) {
                    break // 没有找到，检查下一个 emoji
                }

                // 标记发生了替换
                changed = true

                // 创建 ImageSpan，使用 ALIGN_BASELINE 对齐
                val resId = EmojiMapHelper.getEmojiValue(emoji)
                val imageSpan = ImageSpanCompat.newImageSpan(AppApplication.getInstance(), resId, ImageSpanCompat.ALIGN_CENTER)

                // 在 SpannableStringBuilder 上应用 ImageSpan
                // 仅对当前匹配到的短代码文本应用 Span
                builder.setSpan(imageSpan, index, index + emoji.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                // 更新下次查找的起始位置
                lastIndex = index + emoji.length
            }
        }

        // 5. 根据是否进行了替换返回结果
        return if (changed) {
            // 如果进行了替换，返回带 Span 的 builder
            builder
        } else {
            // 如果没有匹配到任何完整的 emoji 短代码，返回 null，表示接受原始输入
            null
        }
    }
}