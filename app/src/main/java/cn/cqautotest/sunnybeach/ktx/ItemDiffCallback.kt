package cn.cqautotest.sunnybeach.ktx

import androidx.recyclerview.widget.DiffUtil

fun <T : Any> itemDiffCallback(
    areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
) = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areItemsTheSame.invoke(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = areContentsTheSame.invoke(oldItem, newItem)
}