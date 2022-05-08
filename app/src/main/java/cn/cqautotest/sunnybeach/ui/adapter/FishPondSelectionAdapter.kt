package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.FishPondSelectionItemBinding
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
class FishPondSelectionAdapter :
    RecyclerView.Adapter<FishPondSelectionAdapter.FishPondSelectionViewHolder>() {

    inner class FishPondSelectionViewHolder(val binding: FishPondSelectionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var mItemClickListener: (item: FishPondTopicList.TopicItem, position: Int) -> Unit =
        { _, _ -> }

    private val mData = FishPondTopicList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FishPondTopicList.TopicItem>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (item: FishPondTopicList.TopicItem, position: Int) -> Unit) {
        mItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishPondSelectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FishPondSelectionItemBinding.inflate(inflater, parent, false)
        return FishPondSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FishPondSelectionViewHolder, position: Int) {
        val item = mData[position]
        val binding = holder.binding
        Glide.with(holder.itemView).load(item.cover).into(binding.ivTopicCover)
        binding.tvTopicName.text = item.topicName
        binding.tvTopicDesc.text = item.description
        binding.btnChoose.setRoundRectBg(color = Color.parseColor("#F4F5F5"), 4.dp)
        binding.btnChoose.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
    }

    override fun getItemCount(): Int = mData.size
}