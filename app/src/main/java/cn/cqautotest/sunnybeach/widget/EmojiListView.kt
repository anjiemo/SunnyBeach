package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ui.adapter.EmojiAdapter
import cn.cqautotest.sunnybeach.util.EmojiMapHelper
import cn.cqautotest.sunnybeach.util.GridSpaceItemDecoration

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : emoji 表情列表 View
 */
class EmojiListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val mBilibiliAdapter = EmojiAdapter()

    init {
        layoutManager = GridLayoutManager(context, 7)
        val concatAdapter = ConcatAdapter(mBilibiliAdapter)
        adapter = concatAdapter
        addItemDecoration(GridSpaceItemDecoration(5.dp))
        mBilibiliAdapter.setData(loadBilibiliEmojiList())
    }

    private fun loadBilibiliEmojiList(): List<String> {
        val emojiList = arrayListOf<String>()
        EmojiMapHelper.bilibiliEmojiMap.onEach {
            val emoji = it.key
            emojiList.add(emoji)
        }
        return emojiList
    }

    fun setOnEmojiClickListener(listener: (emoji: String, position: Int) -> Unit) {
        mBilibiliAdapter.setOnItemClickListener(listener)
    }
}