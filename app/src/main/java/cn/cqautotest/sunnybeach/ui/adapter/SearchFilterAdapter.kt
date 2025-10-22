package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.SearchFilterItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.model.SearchFilterItem

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/07/08
 * desc   : 搜索过滤条件适配器
 */
class SearchFilterAdapter : RecyclerView.Adapter<SearchFilterAdapter.SearchFilterViewHolder>() {

    private val mData = arrayListOf<SearchFilterItem>()
    private var mOnItemClickListener: (item: SearchFilterItem, position: Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFilterViewHolder = SearchFilterViewHolder(parent)

    override fun onBindViewHolder(holder: SearchFilterViewHolder, position: Int) {
        val itemView = holder.itemView
        val item = mData[position]
        itemView.setOnClickListener {
            mOnItemClickListener.invoke(item, position)
        }
        holder.onBinding(item, position)
    }

    override fun onBindViewHolder(holder: SearchFilterViewHolder, position: Int, payloads: List<Any?>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        val item = mData[position]
        payloads.find { it == DIFF_CHECKED }?.let {
            holder.onBinding(item, position, it)
        }
    }

    override fun getItemCount(): Int = mData.size

    fun setData(data: List<SearchFilterItem>) {
        mData.clear()
        mData.addAll(data)
        notifyItemRangeChanged(0, mData.count())
    }

    fun setItem(item: SearchFilterItem, position: Int) {
        mData.forEach { it.isChecked = it.id == item.id }
        notifyItemRangeChanged(0, mData.count(), DIFF_CHECKED)
    }

    fun getSelectedItem(): SearchFilterItem? = mData.find { it.isChecked }

    fun setOnItemClickListener(listener: (item: SearchFilterItem, position: Int) -> Unit) {
        mOnItemClickListener = listener
    }

    inner class SearchFilterViewHolder(val binding: SearchFilterItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<SearchFilterItemBinding>())

        fun onBinding(item: SearchFilterItem, position: Int) {
            with(binding) {
                tvText.text = item.text
                binding.ivChcked.isVisible = item.isChecked
            }
        }

        fun onBinding(item: SearchFilterItem, position: Int, payload: Any) {
            when (payload) {
                DIFF_CHECKED -> {
                    binding.ivChcked.isVisible = item.isChecked
                }
            }
        }
    }

    companion object {

        const val DIFF_CHECKED = 0x001
    }
}