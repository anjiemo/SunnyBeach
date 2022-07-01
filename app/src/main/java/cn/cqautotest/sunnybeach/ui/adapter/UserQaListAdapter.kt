package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.UserQaListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.UserQa
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户回答列表的适配器
 */
class UserQaListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserQa.Content, UserQaListAdapter.QaListViewHolder>(diffCallback) {

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    inner class QaListViewHolder(val binding: UserQaListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<UserQaListItemBinding>())

        fun onBinding(item: UserQa.Content?, position: Int) {
            item ?: return
            with(binding) {
                tvQaTitle.text = item.wendaTitle
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.wendaComment.publishTime, mSdf)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: UserQaListAdapter.QaListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QaListViewHolder = QaListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<UserQa.Content>({ oldItem, newItem -> oldItem.wendaComment.id == newItem.wendaComment.id }) { oldItem, newItem -> oldItem == newItem }
    }
}