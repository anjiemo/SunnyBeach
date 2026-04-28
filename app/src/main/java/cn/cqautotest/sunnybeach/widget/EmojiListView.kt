package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ui.adapter.EmojiAdapter
import cn.cqautotest.sunnybeach.widget.recyclerview.UniversalSpaceDecoration

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : emoji 表情列表 View
 */
class EmojiListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val mEmojiAdapter = EmojiAdapter()

    init {
        layoutManager = GridLayoutManager(context, 7)
        adapter = mEmojiAdapter
        addItemDecoration(UniversalSpaceDecoration(10.dp))
    }

    fun setEmojiList(emojiList: List<String>) {
        mEmojiAdapter.setData(emojiList)
    }

    fun setOnEmojiClickListener(listener: (emoji: String, position: Int) -> Unit) {
        mEmojiAdapter.setOnItemClickListener(listener)
    }
}