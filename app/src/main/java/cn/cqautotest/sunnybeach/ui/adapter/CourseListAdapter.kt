package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.Color
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CourseListItemBinding
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.course.Course
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/22
 * desc   : 课程列表的适配器
 */

class CourseListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<Course.CourseItem, CourseListAdapter.CourseViewHolder>(diffCallback) {

    inner class CourseViewHolder(val binding: CourseListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<CourseListItemBinding>())

        fun onBinding(item: Course.CourseItem?, position: Int) {
            item ?: return
            with(binding) {
                GlideApp.with(context)
                    .load(item.cover)
                    .into(ivCover)
                tvTitle.text = item.title
                ivAvatar.loadAvatar(false, item.avatar)
                tvNickName.text = item.teacherName
                val price = item.price
                val isFree = price.isZero
                slvPrice.text = if (isFree) "免费" else "¥ ${item.price}"
                slvPrice.setTextColor(Color.parseColor(if (isFree) "#48D044" else "#007BFF"))
            }
        }
    }

    override fun onViewAttachedToWindow(holder: CourseViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: CourseListAdapter.CourseViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder = CourseViewHolder(parent)

    companion object {
        private val diffCallback =
            itemDiffCallback<Course.CourseItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}