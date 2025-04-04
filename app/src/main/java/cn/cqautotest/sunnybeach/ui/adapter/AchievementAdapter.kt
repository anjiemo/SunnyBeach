package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.collection.set
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CreationCenterAchievementItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.setRoundRectBg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import java.text.DecimalFormat

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/08
 * desc   : 个人中心成就数据的适配器
 */
class AchievementAdapter(private val adapterDelegate: AdapterDelegate) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    // SparseArrays 将整数映射到对象。
    // 与普通的对象数组不同，索引中可能存在间隙。
    // 与使用 HashMap 将整数映射到对象相比，它的内存效率更高，因为它避免了自动装箱键，并且它的数据结构不依赖于每个映射的额外条目对象。
    // 优点：SparseArrayCompat 其实是一个 Map 容器，它使用了一套算法优化了hashMap，可以节省至少50%的缓存.
    // 缺点：只针对下面类型 key: Integer; value: object
    // 参考：https://developer.android.google.cn/reference/androidx/collection/SparseArrayCompat
    private val mData = SparseArrayCompat<Pair<Int, Int>>(4)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Pair<Int, Int>>) {
        // We currently only support collections of size 4.
        val dataSize = data.size
        require(dataSize == 4) { "We need a collection of size 4, your collection is of size $dataSize" }
        mData.clear()
        repeat(4) { mData[it] = data[it] }
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: CreationCenterAchievementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<CreationCenterAchievementItemBinding>())

        @SuppressLint("SetTextI18n")
        fun onBinding(item: Pair<Int, Int>, position: Int) {
            itemView.setRoundRectBg(getColor(position))
            binding.tvAchievementTitle.text = position.getTitle()
            binding.tvTotalNum.text = DecimalFormat.getInstance().format(item.first)
            binding.tvChanges.text = "较前日 ${if (item.second == 0) "--" else " ▲ " + item.second}"
        }

        private fun getColor(position: Int) = when (position) {
            0 -> "#B0D7F8".toColorInt()
            1 -> "#C9B3E1".toColorInt()
            2 -> "#E7C9E1".toColorInt()
            else -> "#F4E5BA".toColorInt()
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
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        val item = mData.valueAt(position)
        holder.onBinding(item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = mData.size()
}