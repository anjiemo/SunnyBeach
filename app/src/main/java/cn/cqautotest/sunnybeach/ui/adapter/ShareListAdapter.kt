package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.ShareListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.UserShare
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 用户分享列表的适配器
 */
class ShareListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserShare.Content, ShareListAdapter.ShareListViewHolder>(diffCallback) {

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    inner class ShareListViewHolder(val binding: ShareListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        constructor(parent: ViewGroup) : this(parent.asViewBinding<ShareListItemBinding>())
    }

    override fun onViewAttachedToWindow(holder: ShareListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShareListAdapter.ShareListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val tvShareTitle = binding.tvShareTitle
        val tvDesc = binding.tvDesc
        itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        tvShareTitle.text = item.title
        tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareListViewHolder = ShareListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<UserShare.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}