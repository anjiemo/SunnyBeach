package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.VipUserListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.model.VipUserInfoSummary
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/08
 * desc   : Vip 用户列表适配器
 */
class VipUserListAdapter(private val adapterDelegate: AdapterDelegate) : RecyclerView.Adapter<VipUserListAdapter.ViewHolder>() {

    private val mData = arrayListOf<VipUserInfoSummary>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<VipUserInfoSummary>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun getData() = mData.toList()

    fun getDataSource() = mData

    inner class ViewHolder(val binding: VipUserListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<VipUserListItemBinding>())

        fun onBinding(item: VipUserInfoSummary, position: Int) {
            with(binding) {
                ivAvatar.loadAvatar(true, item.avatar)
                tvNickName.text = item.nickname
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.itemView.setOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        holder.onBinding(item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = mData.size
}