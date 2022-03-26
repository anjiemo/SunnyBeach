package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setDefaultEmojiParser

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : 表情列表选择适配器
 */
class EmojiAdapter : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    private var mOnItemClickListener: (emoji: String, position: Int) -> Unit = { _, _ -> }
    private val mData = arrayListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<String>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (emoji: String, position: Int) -> Unit) {
        mOnItemClickListener = listener
    }

    class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmoji: TextView = itemView.findViewWithTag(EMOJI_TAG)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val itemView = FrameLayout(parent.context)
        itemView.layoutParams = ViewGroup.LayoutParams(40.dp, 40.dp)
        val tvEmoji = TextView(parent.context).apply {
            tag = EMOJI_TAG
            gravity = Gravity.CENTER
            textSize = 22f
        }
        tvEmoji.setDefaultEmojiParser()
        itemView.addView(tvEmoji)
        return EmojiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val itemView = holder.itemView
        val item = mData[position]
        itemView.setOnClickListener {
            mOnItemClickListener.invoke(item, position)
        }
        holder.tvEmoji.text = item
    }

    override fun getItemCount(): Int = mData.size

    companion object {
        private const val EMOJI_TAG = "EMOJI"
    }
}