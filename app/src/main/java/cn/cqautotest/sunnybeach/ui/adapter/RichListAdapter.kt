package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.RichListItemBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.RichList
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 富豪榜列表适配器
 */
class RichListAdapter :
    PagingDataAdapter<RichList.RichUserItem, RichListAdapter.RichListViewHolder>(diffCallback) {

    inner class RichListViewHolder(val binding: RichListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var mItemClickListener: (item: RichList.RichUserItem, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemClickListener(listener: (item: RichList.RichUserItem, position: Int) -> Unit) {
        mItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RichListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RichListItemBinding.inflate(inflater, parent, false)
        return RichListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RichListViewHolder, position: Int) {
        val itemView = holder.itemView
        val context = itemView.context
        val item = getItem(position) ?: return
        val binding = holder.binding
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
        binding.tvNumber.text = (position + 1).toString()
        binding.flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(holder.itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(binding.ivAvatar)
        val nickNameColor = if (item.vip) {
            R.color.pink
        } else {
            R.color.black
        }
        binding.tvNickName.setTextColor(ContextCompat.getColor(context, nickNameColor))
        binding.tvNickName.text = item.nickname
        binding.tvDesc.text = "财富值：${item.sob}"
        binding.ivMedals.apply {
            when (position) {
                0 -> setImageResource(R.mipmap.ic_rank_1)
                1 -> setImageResource(R.mipmap.ic_rank_2)
                2 -> setImageResource(R.mipmap.ic_rank_3)
                else -> setImageDrawable(null)
            }
        }
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<RichList.RichUserItem>({ oldItem, newItem -> oldItem.userId == newItem.userId }) { oldItem, newItem -> oldItem == newItem }
    }
}