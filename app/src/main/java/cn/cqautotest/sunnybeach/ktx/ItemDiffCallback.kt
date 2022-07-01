package cn.cqautotest.sunnybeach.ktx

import androidx.recyclerview.widget.DiffUtil

/**
 * Used to simplify the use of DiffUtil.
 */
inline fun <T : Any> itemDiffCallback(
    crossinline areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    crossinline areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
) = object : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areItemsTheSame.invoke(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = areContentsTheSame.invoke(oldItem, newItem)
}