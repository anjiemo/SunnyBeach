package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ui.adapter.EmojiCategoryAdapter
import cn.cqautotest.sunnybeach.util.EmojiMapHelper

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : 表情面板 View
 */
class EmojiPanelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    companion object {
        val TAB_HEIGHT = 50.dp
    }

    private val mCategoryAdapter = EmojiCategoryAdapter()
    private val mEmojiListView = EmojiListView(context)
    private val mCategories = arrayListOf<EmojiCategory>()

    data class EmojiCategory(
        val firstEmojiResId: Int,
        val emojis: List<String>
    )

    init {
        orientation = VERTICAL
        setupCategoryRecyclerView()
        setupEmojiListView()
        loadData()
    }

    private fun setupCategoryRecyclerView() {
        val recyclerView = RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, TAB_HEIGHT)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = mCategoryAdapter
        }
        mCategoryAdapter.setOnItemClickListener { position ->
            mCategoryAdapter.setSelectedPosition(position)
            mEmojiListView.setEmojiList(mCategories[position].emojis)
        }
        addView(recyclerView)
    }

    private fun setupEmojiListView() {
        mEmojiListView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
        addView(mEmojiListView)
    }

    private fun loadData() {
        val bilibiliEmojis = EmojiMapHelper.bilibiliEmojiMap.keys.toList()
        val douyinEmojis = EmojiMapHelper.douyinEmojiMap.keys.toList()

        mCategories.add(EmojiCategory(EmojiMapHelper.bilibiliEmojiMap.values.first(), bilibiliEmojis))
        mCategories.add(EmojiCategory(EmojiMapHelper.douyinEmojiMap.values.first(), douyinEmojis))

        mCategoryAdapter.setData(mCategories.map { it.firstEmojiResId })
        if (mCategories.isNotEmpty()) {
            mEmojiListView.setEmojiList(mCategories[0].emojis)
            mCategoryAdapter.setSelectedPosition(0)
        }
    }

    fun setCategories(categories: List<EmojiCategory>) {
        mCategories.clear()
        mCategories.addAll(categories)
        mCategoryAdapter.setData(mCategories.map { it.firstEmojiResId })
        if (mCategories.isNotEmpty()) {
            mEmojiListView.setEmojiList(mCategories[0].emojis)
            mCategoryAdapter.setSelectedPosition(0)
        }
    }

    fun setOnEmojiClickListener(listener: (emoji: String, position: Int) -> Unit) {
        mEmojiListView.setOnEmojiClickListener(listener)
    }
}
