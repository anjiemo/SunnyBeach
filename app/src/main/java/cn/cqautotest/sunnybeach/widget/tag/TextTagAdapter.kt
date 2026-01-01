package cn.cqautotest.sunnybeach.widget.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.cqautotest.sunnybeach.databinding.ItemTextTagBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/01
 * desc   : 文本标签适配器实现
 *
 * 配合 [SingleLineTagContainer] 使用，通过泛型 T=String 简化数据处理。
 * 内部通过 [BindingTagViewHolder] 自动处理 ViewBinding 的持有。
 */
class TextTagAdapter(private val onTagClick: ((item: String) -> Unit)? = null) :
    TagViewAdapter<String, BindingTagViewHolder<ItemTextTagBinding>> {

    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingTagViewHolder<ItemTextTagBinding> {
        val binding = ItemTextTagBinding.inflate(inflater, parent, false)
        return BindingTagViewHolder(binding)
    }

    override fun bindViewHolder(holder: BindingTagViewHolder<ItemTextTagBinding>, item: String, position: Int) {
        holder.itemView.setOnClickListener { onTagClick?.invoke(item) }
        val binding = holder.binding
        binding.tvTagName.text = item
    }
}