package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ui.adapter.EmojiAdapter
import cn.cqautotest.sunnybeach.util.GridSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import com.blankj.utilcode.util.ResourceUtils
import org.json.JSONObject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : emoji 表情列表 View
 */
class EmojiListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val mAdapter = EmojiAdapter()

    init {
        layoutManager = GridLayoutManager(context, 7)
        adapter = mAdapter
        addItemDecoration(GridSpaceItemDecoration(5.dp))
        mAdapter.setData(loadDefaultEmojiList())
    }

    private fun loadDefaultEmojiList(): List<String> {
        val emojiList = arrayListOf<String>()
        val json = ResourceUtils.readAssets2String("emoji/emoji.json")
        val jsonObject = JSONObject(json)
        jsonObject.keys().forEach { key ->
            jsonObject.optJSONArray(key)?.let { jsonArray ->
                for (index in 0..jsonArray.length()) {
                    val emoji = jsonArray.optString(index)
                    if (TextUtils.isEmpty(emoji).not()) {
                        emojiList.add(emoji)
                    }
                }
            }
        }
        return emojiList
    }

    fun setOnEmojiClickListener(listener: (emoji: String, position: Int) -> Unit) {
        mAdapter.setOnItemClickListener(listener)
    }
}