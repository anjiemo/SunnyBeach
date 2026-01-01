package cn.cqautotest.sunnybeach.widget.tag

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/01
 * desc   : 专为 [ViewBinding] 设计的通用标签 [ViewHolder] 基类。
 *
 * 该类通过持有具体的 ViewBinding 实例 [binding]，简化了子类对布局控件的访问。
 * @param VB 具体的视图绑定类类型，必须继承自 [ViewBinding]。
 * @property binding 通过 ViewBinding 生成的布局绑定实例，可直接操作 XML 中的控件。
 */
class BindingTagViewHolder<VB : ViewBinding>(val binding: VB) : TagViewHolder(binding.root)