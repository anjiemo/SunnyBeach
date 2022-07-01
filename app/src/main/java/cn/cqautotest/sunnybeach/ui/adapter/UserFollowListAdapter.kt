package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FollowListItemBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserFollow
import cn.cqautotest.sunnybeach.other.FriendsStatus
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户关注的用户列表的适配器
 */
class UserFollowListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserFollow.UserFollowItem, UserFollowListAdapter.QaListViewHolder>(diffCallback) {

    inner class QaListViewHolder(val binding: FollowListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<FollowListItemBinding>())

        fun onBinding(item: UserFollow.UserFollowItem?, position: Int) {
            item ?: return
            with(binding) {
                ivAvatar.loadAvatar(item.vip, item.avatar)
                tvNickName.text = item.nickname
                val nickNameColor = if (item.vip) ContextCompat.getColor(context, R.color.pink)
                else Color.BLACK
                tvNickName.setTextColor(nickNameColor)
                tvDesc.text = item.sign ?: context.getString(R.string.not_sign_text_tips)
                val state = FriendsStatus.valueOfCode(item.relative)
                val userId = item.userId
                tvFollow.text = state.desc
                val disableTextColor = ContextCompat.getColor(context, R.color.btn_text_disable_color)
                val disableBgColor = ContextCompat.getColor(context, R.color.btn_bg_disable_color)
                val followColor = if (UserManager.isCurrUser(userId)) disableBgColor else state.color
                tvFollow.setRoundRectBg(followColor, 3.dp)
                tvFollow.setTextColor(if (UserManager.isCurrUser(userId)) disableTextColor else Color.WHITE)
                tvFollow.isEnabled = UserManager.isNotCurrUser(userId)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: UserFollowListAdapter.QaListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QaListViewHolder = QaListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<UserFollow.UserFollowItem>({ oldItem, newItem -> oldItem.userId == newItem.userId }) { oldItem, newItem -> oldItem == newItem }
    }
}