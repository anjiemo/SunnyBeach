package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.dp

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : 表情分类适配器
 */
class EmojiCategoryAdapter : RecyclerView.Adapter<EmojiCategoryAdapter.ViewHolder>() {

    private val mData = arrayListOf<Int>()
    private var mOnItemClickListener: (position: Int) -> Unit = {}
    private var mSelectedPosition = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Int>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        mOnItemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPosition(position: Int) {
        mSelectedPosition = position
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, val ivIcon: ImageView) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(50.dp, 36.dp).apply {
                gravity = Gravity.CENTER
            }
            setPadding(12.dp, 8.dp, 12.dp, 8.dp)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundResource(R.drawable.selector_emoji_category)
        }
        val frameLayout = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(50.dp, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(imageView)
        }
        return ViewHolder(frameLayout, imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resId = mData[position]
        holder.ivIcon.setImageResource(resId)
        holder.ivIcon.isSelected = mSelectedPosition == position
        holder.itemView.setOnClickListener {
            mOnItemClickListener(position)
        }
    }

    override fun getItemCount(): Int = mData.size
}
