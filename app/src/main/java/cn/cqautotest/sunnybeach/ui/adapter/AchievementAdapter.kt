package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CreationCenterAchievementItemBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/08
 * desc   : 个人中心成就数据的适配器
 */
class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    private val mData = arrayListOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Pair<Int, Int>>) {
        data.checkArgs()
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    private fun List<Pair<Int, Int>>.checkArgs() {
        // We currently only support collections of size 4.
        if (size != 4) throw IllegalArgumentException("We need a collection of size 4, your collection is of size $size")
    }

    inner class ViewHolder(val binding: CreationCenterAchievementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(item: Pair<Int, Int>, position: Int) {
            binding.tvAchievementTitle.text = position.getTitle()
            binding.tvTotalNum.text = item.first.toString()
            binding.tvChanges.text = "较前日 ▲ ${item.second}"
        }

        private fun Int.getTitle() = when (this) {
            0 -> "阅读总量"
            1 -> "获赞总量"
            2 -> "粉丝总量"
            3 -> "Sob币"
            else -> ""
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.binding(item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CreationCenterAchievementItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = mData.size
}