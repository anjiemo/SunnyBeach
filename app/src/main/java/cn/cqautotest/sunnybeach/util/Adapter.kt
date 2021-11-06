package cn.cqautotest.sunnybeach.util

import androidx.recyclerview.widget.RecyclerView

/**
 * Returns `true` if this adapter content is not empty.
 */
inline fun <reified VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.isNotEmpty() =
    itemCount != 0

/**
 * Returns `true` if this adapter content is empty.
 */
inline fun <reified VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.isEmpty() =
    itemCount == 0
