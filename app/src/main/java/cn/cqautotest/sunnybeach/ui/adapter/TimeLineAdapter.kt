package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.TimeLineItemBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/01/17
 * desc   : 时间轴适配器
 */
class TimeLineAdapter : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

    private val mData = arrayListOf<Pair<String, String>>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Pair<String, String>>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    class TimeLineViewHolder(val binding: TimeLineItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TimeLineItemBinding.inflate(inflater, parent, false)
        return TimeLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        val item = mData[position]
        val binding = holder.binding
        binding.tvMonth.text = item.first
        binding.tvDesc.text = item.second
    }

    override fun getItemCount(): Int = mData.size
}