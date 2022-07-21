package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.RichListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.RichList
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.bumptech.glide.Glide
import com.dylanc.longan.application

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 富豪榜列表适配器
 */
class RichListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<RichList.RichUserItem, RichListAdapter.RichListViewHolder>(diffCallback) {

    inner class RichListViewHolder(val binding: RichListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<RichListItemBinding>())

        @SuppressLint("SetTextI18n")
        fun onBinding(item: RichList.RichUserItem?, position: Int) {
            item ?: return
            with(binding) {
                tvNumber.text = (position + 1).toString()
                tvNumber.setTextColor(getTextColorByPosition(position))
                flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
                Glide.with(itemView)
                    .load(item.avatar)
                    .placeholder(R.mipmap.ic_default_avatar)
                    .error(R.mipmap.ic_default_avatar)
                    .circleCrop()
                    .into(ivAvatar)
                val nickNameColor = if (item.vip) R.color.pink else R.color.black
                tvNickName.setTextColor(ContextCompat.getColor(context, nickNameColor))
                tvNickName.text = item.nickname
                tvDesc.text = "财富值：${item.sob}"
                ivMedals.apply {
                    when (position) {
                        0 -> setImageResource(R.mipmap.ic_rank_1)
                        1 -> setImageResource(R.mipmap.ic_rank_2)
                        2 -> setImageResource(R.mipmap.ic_rank_3)
                        else -> setImageDrawable(null)
                    }
                }
            }
        }

        private fun getTextColorByPosition(position: Int): Int = when (position) {
            0 -> Color.parseColor("#e3a815")
            1 -> Color.parseColor("#b8b8b8")
            2 -> Color.parseColor("#cc947a")
            else -> ContextCompat.getColor(application, R.color.default_font_color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RichListViewHolder = RichListViewHolder(parent)

    override fun onBindViewHolder(holder: RichListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        holder.onBinding(getItem(position), holder.bindingAdapterPosition)
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<RichList.RichUserItem>({ oldItem, newItem -> oldItem.userId == newItem.userId }) { oldItem, newItem -> oldItem == newItem }
    }
}