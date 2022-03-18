package cn.cqautotest.sunnybeach.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/10
 * desc   : 摸鱼话题分类适配器
 */
class FishPondAdapter : BaseQuickAdapter<FishPondTopicList.TopicItem, BaseViewHolder>(R.layout.fish_pond_topic_list_item),
    LoadMoreModule, DraggableModule {
    override fun convert(holder: BaseViewHolder, item: FishPondTopicList.TopicItem) {
        holder.run {
            val ivCover = getView<ImageView>(R.id.iv_topic_cover)
            val tvTopicName = getView<TextView>(R.id.tv_topic_name)
            Glide.with(holder.itemView).load(item.cover).into(ivCover)
            tvTopicName.text = item.topicName
        }
    }
}