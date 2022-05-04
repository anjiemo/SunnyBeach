package cn.cqautotest.sunnybeach.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CourseChapterChildItemBinding
import cn.cqautotest.sunnybeach.databinding.CourseChapterGroupItemBinding
import cn.cqautotest.sunnybeach.model.course.CourseChapter
import cn.cqautotest.sunnybeach.util.setFixOnClickListener

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/23
 * desc   : 课程章节列表的适配器
 */
class CourseChapterListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<CourseChapterListAdapter.Type, RecyclerView.ViewHolder>(CourseChapterDiffCallback()) {

    class CourseChapterDiffCallback : DiffUtil.ItemCallback<Type>() {
        override fun areItemsTheSame(
            oldItem: Type,
            newItem: Type
        ): Boolean {
            return when (oldItem) {
                is GroupType -> (oldItem as? CourseChapter.CourseChapterItem)?.id == (newItem as? CourseChapter.CourseChapterItem)?.id
                is ChildType -> (oldItem as? CourseChapter.CourseChapterItem.Children)?.id == (newItem as? CourseChapter.CourseChapterItem.Children)?.id
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: Type,
            newItem: Type
        ): Boolean {
            return when (oldItem) {
                is GroupType -> (oldItem as? CourseChapter.CourseChapterItem) == (newItem as? CourseChapter.CourseChapterItem)
                is ChildType -> (oldItem as? CourseChapter.CourseChapterItem.Children) == (newItem as? CourseChapter.CourseChapterItem.Children)
                else -> false
            }
        }
    }

    private var mItemClickListener: (item: CourseChapter.CourseChapterItem.Children, position: Int) -> Unit = { _, _ -> }

    fun setOnItemClickListener(block: (item: CourseChapter.CourseChapterItem.Children, position: Int) -> Unit) {
        mItemClickListener = block
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    inner class CourseChapterListViewHolder(val binding: CourseChapterGroupItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBinding(item: CourseChapter.CourseChapterItem) {
            binding.tvTitle.text = item.title
            binding.tvDesc.text = item.description
        }
    }

    inner class CourseChapterChildrenListViewHolder(val binding: CourseChapterChildItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBinding(item: CourseChapter.CourseChapterItem.Children, position: Int) {
            itemView.setFixOnClickListener {
                mItemClickListener.invoke(item, position)
            }
            binding.tvTitle.text = item.title
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return 0x000
        val clazz = item::class.java
        return when {
            GroupType::class.java.isAssignableFrom(clazz) -> GROUP_TYPE
            ChildType::class.java.isAssignableFrom(clazz) -> CHILD_TYPE
            else -> error("are you ok? We can't created other viewType, because we only have two categories.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (getItemViewType(position)) {
            GROUP_TYPE -> {
                (holder as CourseChapterListViewHolder).onBinding(item as CourseChapter.CourseChapterItem)
            }
            CHILD_TYPE -> {
                (holder as CourseChapterChildrenListViewHolder).onBinding(item as CourseChapter.CourseChapterItem.Children, position)
            }
            else -> error("are you ok? Did you implement the creation of this viewHolder?")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            GROUP_TYPE -> {
                val binding = CourseChapterGroupItemBinding.inflate(inflater, parent, false)
                CourseChapterListViewHolder(binding)
            }
            CHILD_TYPE -> {
                val binding = CourseChapterChildItemBinding.inflate(inflater, parent, false)
                CourseChapterChildrenListViewHolder(binding)
            }
            else -> error("are you ok? Did you implement the creation of this viewType?")
        }
    }

    interface Type

    interface ChildType : Type

    interface GroupType : Type

    companion object {

        private const val GROUP_TYPE = 0x001
        private const val CHILD_TYPE = 0x002
    }
}