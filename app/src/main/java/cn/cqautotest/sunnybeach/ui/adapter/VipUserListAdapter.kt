package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.VipUserListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.model.VipUserInfoSummary

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/08
 * desc   : Vip 用户列表适配器
 */
class VipUserListAdapter : RecyclerView.Adapter<VipUserListAdapter.ViewHolder>() {

    private val mData = arrayListOf<VipUserInfoSummary>()
    private var mListener: (item: VipUserInfoSummary, position: Int) -> Unit = { _, _ -> }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<VipUserInfoSummary>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (item: VipUserInfoSummary, position: Int) -> Unit) {
        mListener = listener
    }

    inner class ViewHolder(val binding: VipUserListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<VipUserListItemBinding>())

        fun binding(item: VipUserInfoSummary, position: Int) {
            binding.ivAvatar.loadAvatar(true, item.avatar)
            binding.tvNickName.text = item.nickname
            itemView.setOnClickListener {
                mListener.invoke(item, position)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.binding(item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = mData.size
}