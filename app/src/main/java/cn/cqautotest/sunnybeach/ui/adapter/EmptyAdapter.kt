package cn.cqautotest.sunnybeach.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/01/21
 * desc   : This emptyAdapter is like a hacker.
 * Its existence allows the PagingAdapter to scroll to the top before being refreshed,
 * avoiding the problem that the PagingAdapter cannot return to the top after being refreshed.
 * But it needs to be used in conjunction with ConcatAdapter, and must appear before PagingAdapter.
 * 参考《Scrolling up after refresh with PagingDataAdapter is an impossible task》：
 * https://www.reddit.com/r/androiddev/comments/lb7ys6/scrolling_up_after_refresh_with_pagingdataadapter
 */
class EmptyAdapter(private val itemCount: Int = 1) : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Nothing to do.
    }

    override fun getItemCount(): Int = itemCount
}