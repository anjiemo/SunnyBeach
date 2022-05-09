package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.VipIntroItemBinding
import cn.cqautotest.sunnybeach.ktx.asInflater
import cn.cqautotest.sunnybeach.util.AdapterDataStore
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/02/01
 * desc   : Vip特权介绍适配器
 */
class VipIntroAdapter : RecyclerView.Adapter<VipIntroAdapter.ViewHolder>() {

    private val mAdapterDataStore = AdapterDataStore<VipIntro>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<VipIntro>) = mAdapterDataStore.submitData(data) { notifyDataSetChanged() }

    data class VipIntro(@DrawableRes val resId: Int, val title: String, val desc: String)

    inner class ViewHolder(val context: Context, val binding: VipIntroItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBinding(item: VipIntro, position: Int) {
            val ivIcon = binding.ivIcon
            Glide.with(context)
                .load(item.resId)
                .into(ivIcon)
            binding.tvTitle.text = item.title
            binding.tvDesc.text = item.desc
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val binding = VipIntroItemBinding.inflate(context.asInflater())
        return ViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mAdapterDataStore.getItem(position)
        holder.onBinding(item, position)
    }

    override fun getItemCount(): Int = mAdapterDataStore.getItemCount()
}
