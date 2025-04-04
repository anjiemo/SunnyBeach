package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.FishPondSelectionItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.setRoundRectBg
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/22
 * desc   : 鱼塘选择列表的适配器
 */
class FishPondSelectionAdapter : RecyclerView.Adapter<FishPondSelectionAdapter.FishPondSelectionViewHolder>() {

    inner class FishPondSelectionViewHolder(val binding: FishPondSelectionItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<FishPondSelectionItemBinding>())

        fun onBinding(item: FishPondTopicList.TopicItem, position: Int) {
            with(binding) {
                Glide.with(itemView).load(item.cover).into(ivTopicCover)
                tvTopicName.text = item.topicName
                tvTopicDesc.text = item.description
                btnChoose.setRoundRectBg(color = "#F4F5F5".toColorInt(), 4.dp)
                btnChoose.setFixOnClickListener { mItemClickListener.invoke(item, position) }
            }
        }
    }

    private var mItemClickListener: (item: FishPondTopicList.TopicItem, position: Int) -> Unit =
        { _, _ -> }

    private val mData = FishPondTopicList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FishPondTopicList.TopicItem>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun getData(): List<FishPondTopicList.TopicItem> = mData.toList()

    fun setOnItemClickListener(listener: (item: FishPondTopicList.TopicItem, position: Int) -> Unit) {
        mItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishPondSelectionViewHolder = FishPondSelectionViewHolder(parent)

    override fun onBindViewHolder(holder: FishPondSelectionViewHolder, position: Int) {
        val item = mData[position]
        holder.onBinding(item, position)
    }

    override fun getItemCount(): Int = mData.size
}