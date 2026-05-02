package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.model.FishPondTopicList
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/10
 * desc   : 摸鱼话题分类适配器
 */
class FishPondAdapter : BaseQuickAdapter<FishPondTopicList.TopicItem, QuickViewHolder>(itemDiffCallback({ old, new -> old.id == new.id }, { old, new -> old == new })) {

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: FishPondTopicList.TopicItem?) {
        item?.let {
            val ivCover = holder.getView<ImageView>(R.id.iv_topic_cover)
            val tvTopicName = holder.getView<TextView>(R.id.tv_topic_name)
            Glide.with(holder.itemView.context).load(it.cover).into(ivCover)
            tvTopicName.text = it.topicName
        }
    }

    override fun onCreateViewHolder(context: android.content.Context, parent: ViewGroup, viewType: Int): QuickViewHolder {
        return QuickViewHolder(R.layout.fish_pond_topic_list_item, parent)
    }
}