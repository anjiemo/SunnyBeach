package cn.cqautotest.sunnybeach.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.FishTopicItemBinding
import cn.cqautotest.sunnybeach.ktx.asInflate
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import cn.cqautotest.sunnybeach.other.TextViewTarget
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dev.androidbroadcast.vbpd.viewBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/16
 * desc   : 首页话题列表的适配器
 */
class FishCategoryAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<FishPondTopicList.TopicItem, FishCategoryAdapter.InnerHolder>(diffCallback) {

    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mBinding by viewBinding(FishTopicItemBinding::bind)

        fun onBinding(item: FishPondTopicList.TopicItem?, position: Int) {
            item ?: return
            mBinding.apply {
                tvMenu.apply {
                    Glide.with(itemView)
                        .load(item.cover)
                        .transform(RoundedCorners(4.dp))
                        .into(TextViewTarget(this, drawableSize = 60.dp))
                    text = item.topicName
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {

        return InnerHolder(FishTopicItemBinding.inflate(parent.asInflate()).root)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        holder.onBinding(getItem(position), position)
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<FishPondTopicList.TopicItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}