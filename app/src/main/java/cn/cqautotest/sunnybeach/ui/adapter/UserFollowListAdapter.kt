package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FollowListItemBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.setRoundRectBg
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserFollow
import cn.cqautotest.sunnybeach.other.FriendsStatus

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户关注的用户列表的适配器
 */
class UserFollowListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserFollow.UserFollowItem, UserFollowListAdapter.QaListViewHolder>(
        UserFollowDiffCallback()
    ) {

    class UserFollowDiffCallback : DiffUtil.ItemCallback<UserFollow.UserFollowItem>() {
        override fun areItemsTheSame(
            oldItem: UserFollow.UserFollowItem,
            newItem: UserFollow.UserFollowItem
        ): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(
            oldItem: UserFollow.UserFollowItem,
            newItem: UserFollow.UserFollowItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var mItemClickListener: (item: UserFollow.UserFollowItem, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemClickListener(block: (item: UserFollow.UserFollowItem, position: Int) -> Unit) {
        mItemClickListener = block
    }

    inner class QaListViewHolder(val binding: FollowListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserFollowListAdapter.QaListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val ivAvatar = binding.ivAvatar
        val tvNickName = binding.tvNickName
        val tvDesc = binding.tvDesc
        val tvFollow = binding.tvFollow
        val context = itemView.context
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QaListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FollowListItemBinding.inflate(inflater, parent, false)
        return QaListViewHolder(binding)
    }
}